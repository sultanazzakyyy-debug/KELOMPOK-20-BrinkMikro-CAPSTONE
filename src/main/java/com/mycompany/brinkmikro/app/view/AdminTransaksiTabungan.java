package com.mycompany.brinkmikro.app.view;

import com.mycompany.brinkmikro.app.dao.NasabahDAO;
import com.mycompany.brinkmikro.app.dao.RekeningTabunganDAO;
import com.mycompany.brinkmikro.app.dao.TransaksiTabunganDAO;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class AdminTransaksiTabungan extends javax.swing.JFrame {

    // Aturan bisnis (ubah di sini kalau aslab minta angka lain)
    private static final BigDecimal MIN_SETOR = new BigDecimal("50000");
    private static final BigDecimal MIN_TARIK = new BigDecimal("50000");
    private static final BigDecimal MAX_NOMINAL = new BigDecimal("9999999999999"); // muat di decimal(15,2)

    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private final TransaksiTabunganDAO transaksiDAO = new TransaksiTabunganDAO();

    public AdminTransaksiTabungan() {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("BrinkMikro - Transaksi Tabungan");

        // Sesuaikan teks komponen (tanpa mengubah layout Designer)
        btnAction3.setText("Refresh");
        btnAction3.setBackground(new java.awt.Color(238, 238, 238));
        btnAction3.setForeground(java.awt.Color.BLACK);
        jLabelSearch.setText("Cari No. Rekening:");

        initTableAndSearch();
        try {
            com.mycompany.brinkmikro.app.model.Pengguna userAktif = com.mycompany.brinkmikro.app.config.UserSession.getLoginUser();
            if (userAktif != null) {
                jLabel7.setText(userAktif.getNamaLengkap());
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat session: " + e.getMessage());
        }
        loadDataKeTabel();
    }

    // ================= TABEL & PENCARIAN =================

    private void initTableAndSearch() {
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jTable1.setDefaultEditor(Object.class, null);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.getTableHeader().setResizingAllowed(false);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.setAutoCreateRowSorter(false);

        model = (DefaultTableModel) jTable1.getModel();
        sorter = new TableRowSorter<>(model);
        jTable1.setRowSorter(sorter);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterSearch(); }
            @Override public void removeUpdate(DocumentEvent e) { filterSearch(); }
            @Override public void changedUpdate(DocumentEvent e) { filterSearch(); }
        });
    }

    /** Cari hanya berdasarkan Nomor Rekening (kolom 4). */
    private void filterSearch() {
        if (sorter == null) return;
        String query = txtSearch.getText().trim();
        if (query.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(query), 4));
        }
    }

    private void sesuaikanLebarKolom() {
        try {
            javax.swing.table.TableColumnModel columnModel = jTable1.getColumnModel();
            for (int column = 0; column < jTable1.getColumnCount(); column++) {
                int width = 50;
                javax.swing.table.TableCellRenderer headerRenderer = jTable1.getTableHeader().getDefaultRenderer();
                java.awt.Component headerComp = headerRenderer.getTableCellRendererComponent(
                        jTable1, columnModel.getColumn(column).getHeaderValue(), false, false, 0, column);
                width = Math.max(width, headerComp.getPreferredSize().width + 20);
                for (int row = 0; row < jTable1.getRowCount(); row++) {
                    javax.swing.table.TableCellRenderer renderer = jTable1.getCellRenderer(row, column);
                    java.awt.Component comp = jTable1.prepareRenderer(renderer, row, column);
                    width = Math.max(comp.getPreferredSize().width + 20, width);
                }
                columnModel.getColumn(column).setPreferredWidth(width);
                columnModel.getColumn(column).setMinWidth(width);
                columnModel.getColumn(column).setMaxWidth(width);
            }
        } catch (Exception e) {
            // abaikan jika model belum siap
        }
    }

    private void loadDataKeTabel() {
        DefaultTableModel tblModel = (DefaultTableModel) jTable1.getModel();
        tblModel.setRowCount(0);
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            for (TransaksiTabunganDAO.TransaksiRecord r : transaksiDAO.getAllTransaksi()) {
                tblModel.addRow(new Object[]{
                    String.format("TRX-%04d", r.idTransaksi),
                    r.tanggal == null ? "-" : fmt.format(r.tanggal),
                    r.jenis,
                    "Rp " + String.format("%,.2f", r.jumlah),
                    r.nomorRekening,
                    String.format("TLR-%04d", r.tellerId)
                });
            }
            sesuaikanLebarKolom();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data transaksi: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= FORM SETOR / TARIK =================

    /** Dipakai oleh tombol Setor Tunai dan Tarik Tunai. */
    private void prosesForm(String jenis) {
        try {
            // 1. Dropdown rekening (hanya yang Aktif) + nama nasabah
            Map<String, String> mapNama = new HashMap<>();
            for (NasabahDAO.NasabahRecord n : new NasabahDAO().getAllNasabah()) {
                mapNama.put(n.nik, n.namaNasabah);
            }

            List<String> itemRek = new ArrayList<>();
            for (RekeningTabunganDAO.RekeningRecord r : new RekeningTabunganDAO().getAllRekening()) {
                if ("Aktif".equalsIgnoreCase(String.valueOf(r.statusRekening))) {
                    itemRek.add(r.nomorRekening + " - " + mapNama.getOrDefault(r.nasabahNik, "Tidak Dikenal")
                            + " (Saldo: Rp " + String.format("%,.2f", r.saldo) + ")");
                }
            }
            if (itemRek.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Belum ada rekening berstatus Aktif.", "Peringatan",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Dropdown teller
            List<String[]> tellers = transaksiDAO.getDaftarTeller();
            if (tellers.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Belum ada teller aktif di data pengguna.", "Peringatan",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            String[] itemTeller = new String[tellers.size()];
            for (int i = 0; i < tellers.size(); i++) {
                itemTeller[i] = String.format("TLR-%04d", Integer.parseInt(tellers.get(i)[0]))
                        + " - " + tellers.get(i)[1];
            }

            javax.swing.JComboBox<String> cmbRek = new javax.swing.JComboBox<>(itemRek.toArray(new String[0]));
            javax.swing.JComboBox<String> cmbTeller = new javax.swing.JComboBox<>(itemTeller);
            javax.swing.JTextField txtJumlah = new javax.swing.JTextField();

            // Pra-pilih rekening kalau ada baris tabel yang dipilih
            int sel = jTable1.getSelectedRow();
            if (sel != -1) {
                String noRekDipilih = model.getValueAt(jTable1.convertRowIndexToModel(sel), 4).toString();
                for (int i = 0; i < itemRek.size(); i++) {
                    if (itemRek.get(i).startsWith(noRekDipilih + " - ")) {
                        cmbRek.setSelectedIndex(i);
                        break;
                    }
                }
            }

            BigDecimal minimal = jenis.equals("Setor Tunai") ? MIN_SETOR : MIN_TARIK;
            javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(4, 2, 10, 10));
            panel.add(new javax.swing.JLabel("Jenis Transaksi :")); panel.add(new javax.swing.JLabel(jenis));
            panel.add(new javax.swing.JLabel("Rekening :")); panel.add(cmbRek);
            panel.add(new javax.swing.JLabel("Teller :")); panel.add(cmbTeller);
            panel.add(new javax.swing.JLabel("Jumlah (min Rp " + String.format("%,.0f", minimal) + ") :")); panel.add(txtJumlah);

            int result = JOptionPane.showConfirmDialog(this, panel, jenis, JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) return;

            // 3. Validasi input (hanya yang penting)
            String raw = txtJumlah.getText().trim().replace(".", "").replace(" ", "");
            if (raw.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Jumlah wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!raw.matches("\\d{1,13}")) {
                JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka bulat positif!", "Peringatan",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            BigDecimal jumlah = new BigDecimal(raw);
            if (jumlah.compareTo(minimal) < 0) {
                JOptionPane.showMessageDialog(this,
                        "Minimal " + jenis.toLowerCase() + " adalah Rp " + String.format("%,.0f", minimal) + ".",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (jumlah.compareTo(MAX_NOMINAL) > 0) {
                JOptionPane.showMessageDialog(this, "Jumlah terlalu besar!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String noRek = cmbRek.getSelectedItem().toString().split(" - ")[0];
            int tellerId = Integer.parseInt(
                    cmbTeller.getSelectedItem().toString().split(" - ")[0].replace("TLR-", ""));

            // 4. Konfirmasi
            int ok = JOptionPane.showConfirmDialog(this,
                    jenis + " sebesar Rp " + String.format("%,.2f", jumlah) + "\npada rekening " + noRek
                            + "\n\nLanjutkan?",
                    "Konfirmasi Transaksi", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (ok != JOptionPane.YES_OPTION) return;

            // 5. Proses atomic di DAO
            BigDecimal saldoBaru = transaksiDAO.prosesTransaksi(noRek, jenis, jumlah, tellerId);
            JOptionPane.showMessageDialog(this,
                    jenis + " berhasil!\nSaldo terbaru: Rp " + String.format("%,.2f", saldoBaru),
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadDataKeTabel();

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Transaksi Ditolak", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan:\n" + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= GENERATED CODE (layout tidak diubah) =================

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnNav1 = new javax.swing.JButton();
        btnNav2 = new javax.swing.JButton();
        btnNav3 = new javax.swing.JButton();
        btnNav4 = new javax.swing.JButton();
        btnNav5 = new javax.swing.JButton();
        btnNav6 = new javax.swing.JButton();
        btnNav7 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButtonLogout = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        btnAction1 = new javax.swing.JButton();
        btnAction2 = new javax.swing.JButton();
        btnAction3 = new javax.swing.JButton();
        jLabelSearch = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BrinkMikro - Manajemen Data Pengguna");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 0, 255));
        jLabel3.setText("BrinkMikro");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 130, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("Super Administrator");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 130, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("Menu Navigasi");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 130, -1));

        jLabel2.setBackground(new java.awt.Color(102, 102, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 0, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Admin");
        jLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 0, 255)));
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 20, 70, 20));

        btnNav1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNav1.setText("1. Data Pengguna");
        btnNav1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNav1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNav1ActionPerformed(evt);
            }
        });
        jPanel2.add(btnNav1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 290, 40));

        btnNav2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNav2.setText("2. Data Nasabah");
        btnNav2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNav2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNav2ActionPerformed(evt);
            }
        });
        jPanel2.add(btnNav2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 290, 40));

        btnNav3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNav3.setText("3. Rekening Tabungan");
        btnNav3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNav3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNav3ActionPerformed(evt);
            }
        });
        jPanel2.add(btnNav3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 290, 40));

        btnNav4.setBackground(new java.awt.Color(0, 0, 255));
        btnNav4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNav4.setForeground(new java.awt.Color(255, 255, 255));
        btnNav4.setText("4. Transaksi Tabungan");
        btnNav4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNav4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNav4ActionPerformed(evt);
            }
        });
        jPanel2.add(btnNav4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 260, 290, 40));

        btnNav5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNav5.setText("5. Pengajuan Pembiayaan");
        btnNav5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNav5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNav5ActionPerformed(evt);
            }
        });
        jPanel2.add(btnNav5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 290, 40));

        btnNav6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNav6.setText("6. Data Angsuran");
        btnNav6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNav6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNav6ActionPerformed(evt);
            }
        });
        jPanel2.add(btnNav6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 360, 290, 40));

        btnNav7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNav7.setText("7. Monitoring dan Laporan");
        btnNav7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNav7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNav7ActionPerformed(evt);
            }
        });
        jPanel2.add(btnNav7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 410, 290, 40));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 310, 770));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Manajemen Transaksi Tabungan");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 10, 500, 25));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("Kelola Transaksi Tabungan");
        jPanel4.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 38, 500, 20));

        jButtonLogout.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonLogout.setForeground(new java.awt.Color(255, 0, 0));
        jButtonLogout.setText("Logout");
        jButtonLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLogoutActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonLogout, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 10, 120, 38));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 0, 1040, 70));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        btnAction1.setBackground(new java.awt.Color(0, 0, 255));
        btnAction1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAction1.setForeground(new java.awt.Color(255, 255, 255));
        btnAction1.setText("Setor Tunai");
        btnAction1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAction1ActionPerformed(evt);
            }
        });

        btnAction2.setBackground(new java.awt.Color(0, 204, 0));
        btnAction2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAction2.setForeground(new java.awt.Color(255, 255, 255));
        btnAction2.setText("Tarik Tunai");
        btnAction2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAction2ActionPerformed(evt);
            }
        });

        btnAction3.setBackground(new java.awt.Color(204, 0, 0));
        btnAction3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAction3.setForeground(new java.awt.Color(255, 255, 255));
        btnAction3.setText("Refresh");
        btnAction3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAction3ActionPerformed(evt);
            }
        });

        jLabelSearch.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabelSearch.setText("Cari No. Rekening:");

        txtSearch.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabelSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSearch))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btnAction1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAction2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAction3)))
                .addContainerGap(696, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAction1)
                    .addComponent(btnAction2)
                    .addComponent(btnAction3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 80, 1030, 110));

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID Transaksi", "Tanggal Transaksi", "Jenis Transaksi", "Jumlah", "Nomor Rekening", "ID Teller"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.setGridColor(new java.awt.Color(0, 0, 0));
        jTable1.setSelectionForeground(new java.awt.Color(255, 255, 255));
        jTable1.setShowGrid(true);
        jScrollPane1.setViewportView(jTable1);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 200, 1030, 500));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1366, 768));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>                        

    // ================= NAVIGASI =================

    private void btnNav1ActionPerformed(java.awt.event.ActionEvent evt) {
        new AdminDataPengguna().setVisible(true);
        this.dispose();
    }

    private void btnNav2ActionPerformed(java.awt.event.ActionEvent evt) {
        new AdminDataNasabah().setVisible(true);
        this.dispose();
    }

    private void btnNav3ActionPerformed(java.awt.event.ActionEvent evt) {
        new AdminRekeningTabungan().setVisible(true);   // sebelumnya salah: AdminTransaksiTabungan
        this.dispose();
    }

    private void btnNav4ActionPerformed(java.awt.event.ActionEvent evt) {
        // sudah di halaman ini
    }

    private void btnNav5ActionPerformed(java.awt.event.ActionEvent evt) {
        new AdminPengajuanPembiayaan().setVisible(true);
        this.dispose();
    }

    private void btnNav6ActionPerformed(java.awt.event.ActionEvent evt) {
        new AdminDataAngsuran().setVisible(true);
        this.dispose();
    }

    private void btnNav7ActionPerformed(java.awt.event.ActionEvent evt) {
        new AdminMonitoringDanLaporan().setVisible(true);
        this.dispose();
    }

    // ================= AKSI =================

    private void btnAction1ActionPerformed(java.awt.event.ActionEvent evt) {
        prosesForm("Setor Tunai");
    }

    private void btnAction2ActionPerformed(java.awt.event.ActionEvent evt) {
        prosesForm("Tarik Tunai");
    }

    private void btnAction3ActionPerformed(java.awt.event.ActionEvent evt) {
        txtSearch.setText("");
        loadDataKeTabel();
    }

    private void jButtonLogoutActionPerformed(java.awt.event.ActionEvent evt) {
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout?",
                "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new FormLogin().setVisible(true);
            this.dispose();
        }
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(AdminTransaksiTabungan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminTransaksiTabungan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton btnAction1;
    private javax.swing.JButton btnAction2;
    private javax.swing.JButton btnAction3;
    private javax.swing.JButton btnNav1;
    private javax.swing.JButton btnNav2;
    private javax.swing.JButton btnNav3;
    private javax.swing.JButton btnNav4;
    private javax.swing.JButton btnNav5;
    private javax.swing.JButton btnNav6;
    private javax.swing.JButton btnNav7;
    private javax.swing.JButton jButtonLogout;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabelSearch;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration                   
}
