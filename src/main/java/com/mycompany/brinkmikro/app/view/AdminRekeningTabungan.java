/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.brinkmikro.app.view;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class AdminRekeningTabungan extends javax.swing.JFrame {

    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    public AdminRekeningTabungan() {
        initComponents();
        setLocationRelativeTo(null);
        initTableAndSearch();
        try {
            com.mycompany.brinkmikro.app.model.Pengguna userAktif = com.mycompany.brinkmikro.app.config.UserSession.getLoginUser();
            if (userAktif != null) {
                // jLabel7 adalah label 'Super Administrator' di bawah logo BrinkMikro
                jLabel7.setText(userAktif.getNamaLengkap());
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat session: " + e.getMessage());
        }

        // 2. Load Data ke Tabel
        loadDataKeTabel();
    }

    private void initTableAndSearch() {
    // 1. Pengaturan Scrollbar
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // 2. Kunci Tabel (Tidak bisa diedit, digeser, atau di-resize manual)
        jTable1.setDefaultEditor(Object.class, null); 
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.getTableHeader().setResizingAllowed(false);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        // 3. MATIKAN Auto-Sorter bawaan NetBeans agar tidak bentrok
        jTable1.setAutoCreateRowSorter(false);

        // 4. Pasang Mesin Pencari (Sorter) Manual
        model = (DefaultTableModel) jTable1.getModel();
        sorter = new TableRowSorter<>(model);
        jTable1.setRowSorter(sorter);

        // 5. PASANG KEMBALI Sensor Ketikan (Sangat Krusial!)
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterSearch(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterSearch(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterSearch(); }
        });
    }

    private void filterSearch() {
        if (sorter == null) return;
        
        String query = txtSearch.getText().trim();
        if (query.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(query), 0, 4, 5));
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
            // Abaikan jika model JTable belum siap saat pertama kali diload
        }
    }
    
    private void loadDataKeTabel() {
        DefaultTableModel tblModel = (DefaultTableModel) jTable1.getModel();
        tblModel.setRowCount(0); 
        try {
            com.mycompany.brinkmikro.app.dao.RekeningTabunganDAO dao = new com.mycompany.brinkmikro.app.dao.RekeningTabunganDAO();
            java.util.List<com.mycompany.brinkmikro.app.dao.RekeningTabunganDAO.RekeningRecord> listData = dao.getAllRekening();
            
            // Buat DAO Nasabah untuk mengambil nama berdasarkan NIK
            com.mycompany.brinkmikro.app.dao.NasabahDAO nasabahDAO = new com.mycompany.brinkmikro.app.dao.NasabahDAO();
            java.util.List<com.mycompany.brinkmikro.app.dao.NasabahDAO.NasabahRecord> listNasabah = nasabahDAO.getAllNasabah();
            
            // Ubah list nasabah ke Map agar pencarian nama berdasarkan NIK sangat cepat (O(1))
            java.util.Map<String, String> mapNamaNasabah = new java.util.HashMap<>();
            for (com.mycompany.brinkmikro.app.dao.NasabahDAO.NasabahRecord n : listNasabah) {
                mapNamaNasabah.put(n.nik, n.namaNasabah);
            }
            
            for (com.mycompany.brinkmikro.app.dao.RekeningTabunganDAO.RekeningRecord r : listData) {
                // Cari nama nasabah otomatis dari Map menggunakan NIK, jika tidak ketemu tulis "Tidak Dikenal"
                String namaNasabah = mapNamaNasabah.getOrDefault(r.nasabahNik, "Tidak Dikenal");
                
                Object[] baris = {
                    r.nomorRekening, 
                    "Rp " + String.format("%,.2f", r.saldo), 
                    r.tanggalPembukaan, 
                    r.statusRekening, 
                    r.nasabahNik,
                    namaNasabah // Kolom ke-5: Otomatis tampil nama nasabah
                };
                tblModel.addRow(baris);
            }
            
            sesuaikanLebarKolom(); 
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data rekening: " + e.getMessage());
        }
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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

        btnNav3.setBackground(new java.awt.Color(0, 0, 255));
        btnNav3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNav3.setForeground(new java.awt.Color(255, 255, 255));
        btnNav3.setText("3. Rekening Tabungan");
        btnNav3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNav3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNav3ActionPerformed(evt);
            }
        });
        jPanel2.add(btnNav3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 290, 40));

        btnNav4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
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
        jLabel5.setText("Manajemen Rekening Tabungan");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 10, 500, 25));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("Kelola Master Rekening Tabungan Nasabah");
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
        btnAction1.setText("Buka Rekening");
        btnAction1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAction1ActionPerformed(evt);
            }
        });

        btnAction2.setBackground(new java.awt.Color(255, 255, 0));
        btnAction2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAction2.setText("Update Rekening");
        btnAction2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAction2ActionPerformed(evt);
            }
        });

        btnAction3.setBackground(new java.awt.Color(204, 0, 0));
        btnAction3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAction3.setForeground(new java.awt.Color(255, 255, 255));
        btnAction3.setText("Hapus Rekening");
        btnAction3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAction3ActionPerformed(evt);
            }
        });

        jLabelSearch.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabelSearch.setText("Cari No. Rek/Nama:");

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
                .addContainerGap(643, Short.MAX_VALUE))
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
                "No. Rekening", "Saldo", "Tanggal Pembukaan", "Status Rekening", "NIK Nasabah", "Nama Nasabah"
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
    }// </editor-fold>//GEN-END:initComponents

    private void btnNav1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNav1ActionPerformed
        new AdminDataPengguna().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnNav1ActionPerformed

    private void btnNav2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNav2ActionPerformed
        new AdminDataNasabah().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnNav2ActionPerformed

    private void btnNav3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNav3ActionPerformed
        new AdminRekeningTabungan().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnNav3ActionPerformed

    private void btnNav4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNav4ActionPerformed
        new AdminTransaksiTabungan().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnNav4ActionPerformed

    private void btnNav5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNav5ActionPerformed
        new AdminPengajuanPembiayaan().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnNav5ActionPerformed

    private void btnNav6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNav6ActionPerformed
        new AdminDataAngsuran().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnNav6ActionPerformed

    private void btnNav7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNav7ActionPerformed
        new AdminMonitoringDanLaporan().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnNav7ActionPerformed

    private void btnAction1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAction1ActionPerformed
        try {
            com.mycompany.brinkmikro.app.dao.NasabahDAO nasabahDAO = new com.mycompany.brinkmikro.app.dao.NasabahDAO();
            java.util.List<com.mycompany.brinkmikro.app.dao.NasabahDAO.NasabahRecord> listNasabah = nasabahDAO.getAllNasabah();
            
            if (listNasabah.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Data Nasabah kosong! Silakan tambah nasabah di menu Data Nasabah terlebih dahulu.");
                return;
            }
            
            String[] nasabahArray = new String[listNasabah.size()];
            for (int i = 0; i < listNasabah.size(); i++) {
                nasabahArray[i] = listNasabah.get(i).nik + " - " + listNasabah.get(i).namaNasabah;
            }

            javax.swing.JPanel panelForm = new javax.swing.JPanel(new java.awt.GridLayout(2, 2, 10, 10));
            javax.swing.JComboBox<String> cmbNik = new javax.swing.JComboBox<>(nasabahArray);
            javax.swing.JComboBox<String> cmbStatus = new javax.swing.JComboBox<>(new String[]{"Aktif", "Perlu Verifikasi", "Nonaktif", "Ditutup"});

            panelForm.add(new javax.swing.JLabel("Pilih Nasabah :")); panelForm.add(cmbNik);
            panelForm.add(new javax.swing.JLabel("Status Awal :")); panelForm.add(cmbStatus);

            int result = JOptionPane.showConfirmDialog(this, panelForm, "Buka Rekening Tabungan Baru", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String tahun = String.valueOf(java.time.Year.now().getValue());
                String randomDigits = String.format("%04d", new java.util.Random().nextInt(1000000));
                String noRekOtomatis = "1001" + tahun + randomDigits;

                String selectedNasabah = cmbNik.getSelectedItem().toString();
                String nikSaja = selectedNasabah.split(" - ")[0];

                com.mycompany.brinkmikro.app.dao.RekeningTabunganDAO dao = new com.mycompany.brinkmikro.app.dao.RekeningTabunganDAO();
                boolean sukses = dao.tambahRekening(noRekOtomatis, nikSaja);
                
                if (sukses) {
                    JOptionPane.showMessageDialog(this, "Rekening berhasil dibuka!\nNo Rekening: " + noRekOtomatis, "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    loadDataKeTabel();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan:\n" + e.getMessage());
        }
    }//GEN-LAST:event_btnAction1ActionPerformed

    private void btnAction2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAction2ActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris rekening terlebih dahulu!");
            return;
        }

        try {
            int modelRow = jTable1.convertRowIndexToModel(selectedRow);
            String currNoRek = model.getValueAt(modelRow, 0).toString(); 
            String currStatus = model.getValueAt(modelRow, 3).toString();
            String currNik = model.getValueAt(modelRow, 4).toString();

            // 1. Tarik Data Nasabah untuk Dropdown
            com.mycompany.brinkmikro.app.dao.NasabahDAO nasabahDAO = new com.mycompany.brinkmikro.app.dao.NasabahDAO();
            java.util.List<com.mycompany.brinkmikro.app.dao.NasabahDAO.NasabahRecord> listNasabah = nasabahDAO.getAllNasabah();
            
            String[] nasabahArray = new String[listNasabah.size()];
            int selectedIndex = 0;
            
            for (int i = 0; i < listNasabah.size(); i++) {
                nasabahArray[i] = listNasabah.get(i).nik + " - " + listNasabah.get(i).namaNasabah;
                // Deteksi otomatis jika NIK di database cocok dengan NIK di baris tabel yang dipilih
                if (listNasabah.get(i).nik.equals(currNik)) {
                    selectedIndex = i;
                }
            }

            // 2. Bangun Form Update
            javax.swing.JPanel panelForm = new javax.swing.JPanel(new java.awt.GridLayout(3, 2, 10, 10));
            
            javax.swing.JTextField txtNoRek = new javax.swing.JTextField(currNoRek);
            txtNoRek.setEnabled(false); 
            
            javax.swing.JComboBox<String> cmbNik = new javax.swing.JComboBox<>(nasabahArray);
            cmbNik.setSelectedIndex(selectedIndex); 
            cmbNik.setEnabled(false);
            
            javax.swing.JComboBox<String> cmbStatus = new javax.swing.JComboBox<>(new String[]{"Aktif", "Perlu Verifikasi", "Nonaktif", "Ditutup"});
            cmbStatus.setSelectedItem(currStatus);

            panelForm.add(new javax.swing.JLabel("Nomor Rekening :")); panelForm.add(txtNoRek);
            panelForm.add(new javax.swing.JLabel("Pilih Nasabah :")); panelForm.add(cmbNik);
            panelForm.add(new javax.swing.JLabel("Status Rekening :")); panelForm.add(cmbStatus);

            int result = JOptionPane.showConfirmDialog(this, panelForm, "Update Rekening (" + currNoRek + ")", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                com.mycompany.brinkmikro.app.dao.RekeningTabunganDAO dao = new com.mycompany.brinkmikro.app.dao.RekeningTabunganDAO();
                boolean sukses = dao.updateStatusRekening(currNoRek, cmbStatus.getSelectedItem().toString());
                
                if (sukses) {
                    JOptionPane.showMessageDialog(this, "Status rekening diperbarui!");
                    loadDataKeTabel();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan:\n" + e.getMessage());
        }
        
    
    }//GEN-LAST:event_btnAction2ActionPerformed

    private void btnAction3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAction3ActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris rekening yang akan dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus rekening tabungan ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int modelRow = jTable1.convertRowIndexToModel(selectedRow);
                String noRek = model.getValueAt(modelRow, 0).toString();
                
                com.mycompany.brinkmikro.app.dao.RekeningTabunganDAO dao = new com.mycompany.brinkmikro.app.dao.RekeningTabunganDAO();
                if (dao.hapusRekening(noRek)) {
                    JOptionPane.showMessageDialog(this, "Rekening berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    loadDataKeTabel();
                }
            } catch (Exception e) {
                // Penanganan cerdas jika rekening terikat transaksi (Tanpa mengubah database)
                String errMsg = e.getMessage().toLowerCase();
                if (errMsg.contains("foreign key") || errMsg.contains("constraint") || errMsg.contains("restricts")) {
                    JOptionPane.showMessageDialog(this, 
                        "Gagal Menghapus Rekening!\n\n" +
                        "Alasan: Rekening ini sudah memiliki riwayat transaksi (Setor/Tarik).\n" +
                        "Sesuai standar perbankan, rekening ber-transaksi tidak boleh dihapus.\n\n" +
                        "Solusi: Gunakan tombol 'Update Rekening' dan ubah statusnya menjadi 'Ditutup'.", 
                        "Penolakan Sistem (Compliance)", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal hapus rekening: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_btnAction3ActionPerformed

    private void jButtonLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLogoutActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new FormLogin().setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_jButtonLogoutActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(AdminRekeningTabungan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminRekeningTabungan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    // End of variables declaration//GEN-END:variables
}
