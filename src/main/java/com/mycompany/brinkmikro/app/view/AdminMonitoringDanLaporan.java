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

public class AdminMonitoringDanLaporan extends javax.swing.JFrame {

    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    // Deklarasi JLabel untuk menampilkan angka di panel Ringkasan
    private javax.swing.JLabel lblTotalPembiayaan;
    private javax.swing.JLabel lblTotalTabungan;
    private javax.swing.JLabel lblLogHariIni;

    public AdminMonitoringDanLaporan() {
        initComponents();
        setLocationRelativeTo(null);
        initTableAndSearch();
        initDashboardLabels();
        
        loadDataKeTabel();
        hitungSummaryDashboard();
    }
    
    private void initDashboardLabels() {
        // Mengunci ukuran ketiga panel agar seragam (Lebar 145px, Tinggi 60px)
        java.awt.Dimension panelSize = new java.awt.Dimension(145, 60);

        jPanel7.setLayout(new java.awt.BorderLayout());
        jPanel7.setPreferredSize(panelSize);
        lblTotalPembiayaan = new javax.swing.JLabel("Rp 0", javax.swing.SwingConstants.CENTER);
        lblTotalPembiayaan.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12)); // Font dikecilkan agar angka muat
        jPanel7.add(new javax.swing.JLabel(" Total Pembiayaan:", javax.swing.SwingConstants.CENTER), java.awt.BorderLayout.NORTH);
        jPanel7.add(lblTotalPembiayaan, java.awt.BorderLayout.CENTER);

        jPanel8.setLayout(new java.awt.BorderLayout());
        jPanel8.setPreferredSize(panelSize);
        lblTotalTabungan = new javax.swing.JLabel("Rp 0", javax.swing.SwingConstants.CENTER);
        lblTotalTabungan.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        jPanel8.add(new javax.swing.JLabel(" Total Tabungan:", javax.swing.SwingConstants.CENTER), java.awt.BorderLayout.NORTH);
        jPanel8.add(lblTotalTabungan, java.awt.BorderLayout.CENTER);

        jPanel9.setLayout(new java.awt.BorderLayout());
        jPanel9.setPreferredSize(panelSize);
        lblLogHariIni = new javax.swing.JLabel("0 Log", javax.swing.SwingConstants.CENTER);
        lblLogHariIni.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        jPanel9.add(new javax.swing.JLabel(" Aktif Hari Ini:", javax.swing.SwingConstants.CENTER), java.awt.BorderLayout.NORTH);
        jPanel9.add(lblLogHariIni, java.awt.BorderLayout.CENTER);
    }

    private void hitungSummaryDashboard() {
        try (java.sql.Connection conn = com.mycompany.brinkmikro.app.config.DatabaseConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            
            // 1. Total Pembiayaan (Tabel pembiayaan sudah ada)
            java.sql.ResultSet rs1 = stmt.executeQuery("SELECT SUM(total_kewajiban) AS total_kredit FROM pembiayaan WHERE status_pembiayaan != 'Lunas'");
            if (rs1.next() && rs1.getBigDecimal("total_kredit") != null) {
                lblTotalPembiayaan.setText("Rp " + String.format("%,.0f", rs1.getDouble("total_kredit")));
            }
            
            // 2. Total Tabungan (Tabel rekening_tabungan sudah ada)
            java.sql.ResultSet rs2 = stmt.executeQuery("SELECT SUM(saldo) AS total_saldo FROM rekening_tabungan");
            if (rs2.next() && rs2.getBigDecimal("total_saldo") != null) {
                lblTotalTabungan.setText("Rp " + String.format("%,.0f", rs2.getDouble("total_saldo")));
            }
            
            // 3. Log Hari Ini (Menghitung dari tabel transaksi_tabungan + angsuran hari ini)
            java.sql.ResultSet rs3 = stmt.executeQuery("SELECT (SELECT COUNT(*) FROM transaksi_tabungan WHERE DATE(tanggal_transaksi) = CURDATE()) + (SELECT COUNT(*) FROM angsuran WHERE DATE(tanggal_bayar) = CURDATE()) AS total_log");
            if (rs3.next()) {
                lblLogHariIni.setText(rs3.getInt("total_log") + " Log");
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat dashboard: " + e.getMessage());
        }
    }

    private void loadDataKeTabel() {
        model.setRowCount(0);
        // TRIK CERDAS: Menggabungkan tabel Transaksi Tabungan dan Angsuran sebagai "Log" 
        // Tanpa perlu membuat tabel audit_log baru di database!
        String sql = "SELECT t.tanggal_transaksi AS waktu, t.teller_id_pengguna, 'Transaksi Tabungan' AS modul, CONCAT(t.jenis_transaksi, ' Rp ', t.jumlah) AS aksi, p.nama_lengkap " +
                     "FROM transaksi_tabungan t LEFT JOIN pengguna p ON t.teller_id_pengguna = p.id_pengguna " +
                     "UNION ALL " +
                     "SELECT a.tanggal_bayar AS waktu, a.teller_id_pengguna, 'Pembayaran Angsuran' AS modul, CONCAT('Bayar Angsuran ke-', a.angsuran_ke, ' Rp ', a.jumlah_bayar) AS aksi, p.nama_lengkap " +
                     "FROM angsuran a LEFT JOIN pengguna p ON a.teller_id_pengguna = p.id_pengguna " +
                     "ORDER BY waktu DESC";
                     
        try (java.sql.Connection conn = com.mycompany.brinkmikro.app.config.DatabaseConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String namaUser = rs.getString("nama_lengkap");
                if (namaUser == null) namaUser = "Sistem/Unknown";
                
                Object[] row = {
                    rs.getString("waktu"),
                    "PGN-" + String.format("%04d", rs.getInt("teller_id_pengguna")) + " (" + namaUser + ")",
                    rs.getString("modul"),
                    rs.getString("aksi"),
                    "127.0.0.1 (Local)", // Teks Statis karena IP tidak disimpan di tabel asli
                    "Berhasil"
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menarik data log gabungan:\n" + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initTableAndSearch() {
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        jTable1.getColumnModel().getColumn(0).setPreferredWidth(150); 
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(160); 
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(130); 
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(350); 
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(120); 
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(100); 
        
        jTable1.setDefaultEditor(Object.class, null); 
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.setAutoCreateRowSorter(false);

        model = (DefaultTableModel) jTable1.getModel();
        sorter = new TableRowSorter<>(model);
        jTable1.setRowSorter(sorter);

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
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(query), 1, 3));
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
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
        jLabel10 = new javax.swing.JLabel();
        jButtonLogout = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        btnAction2 = new javax.swing.JButton();
        jLabelSearch = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BrinkMikro - Monitoring & Laporan Audit Sistem");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 128, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 58, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 120, 130, 60));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 0, 255));
        jLabel3.setText("BrinkMikro");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 130, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Super Administrator");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 130, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
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
        btnNav1.setForeground(new java.awt.Color(0, 0, 0));
        btnNav1.setText("1. Data Pengguna");
        btnNav1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNav1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNav1ActionPerformed(evt);
            }
        });
        jPanel2.add(btnNav1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 290, 40));

        btnNav2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNav2.setForeground(new java.awt.Color(0, 0, 0));
        btnNav2.setText("2. Data Nasabah");
        btnNav2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNav2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNav2ActionPerformed(evt);
            }
        });
        jPanel2.add(btnNav2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 290, 40));

        btnNav3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNav3.setForeground(new java.awt.Color(0, 0, 0));
        btnNav3.setText("3. Rekening Tabungan");
        btnNav3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNav3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNav3ActionPerformed(evt);
            }
        });
        jPanel2.add(btnNav3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 290, 40));

        btnNav4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNav4.setForeground(new java.awt.Color(0, 0, 0));
        btnNav4.setText("4. Transaksi Tabungan");
        btnNav4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNav4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNav4ActionPerformed(evt);
            }
        });
        jPanel2.add(btnNav4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 260, 290, 40));

        btnNav5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNav5.setForeground(new java.awt.Color(0, 0, 0));
        btnNav5.setText("5. Pengajuan Pembiayaan");
        btnNav5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNav5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNav5ActionPerformed(evt);
            }
        });
        jPanel2.add(btnNav5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 290, 40));

        btnNav6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNav6.setForeground(new java.awt.Color(0, 0, 0));
        btnNav6.setText("6. Data Angsuran");
        btnNav6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNav6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNav6ActionPerformed(evt);
            }
        });
        jPanel2.add(btnNav6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 360, 290, 40));

        btnNav7.setBackground(new java.awt.Color(0, 0, 255));
        btnNav7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNav7.setForeground(new java.awt.Color(255, 255, 255));
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

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("Audit Log & Summary Operasional Admin");
        jPanel4.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 38, 500, 20));

        jButtonLogout.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonLogout.setForeground(new java.awt.Color(255, 0, 0));
        jButtonLogout.setText("Logout");
        jButtonLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLogoutActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonLogout, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 10, 120, 38));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Monitoring & Laporan Audit Sistem");
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 10, 500, 25));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 0, 1040, 70));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        btnAction2.setBackground(new java.awt.Color(0, 204, 0));
        btnAction2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAction2.setForeground(new java.awt.Color(255, 255, 255));
        btnAction2.setText("Export Excel");
        btnAction2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAction2ActionPerformed(evt);
            }
        });

        jLabelSearch.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabelSearch.setForeground(new java.awt.Color(0, 0, 0));
        jLabelSearch.setText("Cari Activity/User:");

        txtSearch.setBackground(new java.awt.Color(255, 255, 255));
        txtSearch.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAction2)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabelSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(283, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(btnAction2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 80, 590, 110));

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setForeground(new java.awt.Color(0, 0, 0));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Timestamp", "User / Operator", "Modul", "Aktivitas System", "IP Address", "Status Audit"
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

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Ringkasan Keuangan");

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 128, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 58, Short.MAX_VALUE)
        );

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 116, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 58, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(147, 147, 147)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 80, 430, 110));

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

    private void btnAction2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAction2ActionPerformed
        if (jTable1.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data Audit Log yang bisa diexport!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan Audit ke Excel");
        String tglHariIni = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        fileChooser.setSelectedFile(new java.io.File("Laporan_Audit_BrinkMikro_" + tglHariIni + ".xls"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".xls")) filePath += ".xls";

            try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(filePath))) {
                for (int i = 0; i < jTable1.getColumnCount(); i++) {
                    bw.write(jTable1.getColumnName(i) + "\t");
                }
                bw.newLine();

                for (int i = 0; i < jTable1.getRowCount(); i++) {
                    for (int j = 0; j < jTable1.getColumnCount(); j++) {
                        Object val = jTable1.getValueAt(i, j);
                        String strVal = (val != null) ? val.toString().replace("\n", " ").replace("\t", " ") : "";
                        bw.write(strVal + "\t");
                    }
                    bw.newLine();
                }

                JOptionPane.showMessageDialog(this, "Laporan Excel berhasil disimpan ke:\n" + filePath, "Sukses Export", JOptionPane.INFORMATION_MESSAGE);
                try {
                    if (java.awt.Desktop.isDesktopSupported()) {
                        java.awt.Desktop.getDesktop().open(new java.io.File(filePath));
                    }
                } catch (Exception ex) {}

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengexport file: " + ex.getMessage(), "Error Export", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnAction2ActionPerformed

    private void jButtonLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLogoutActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new FormLogin().setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_jButtonLogoutActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAction2;
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
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabelSearch;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
