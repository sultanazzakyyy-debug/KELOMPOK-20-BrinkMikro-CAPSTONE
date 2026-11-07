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

public class AdminDataNasabah extends javax.swing.JFrame {
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    public AdminDataNasabah() {
        initComponents();
        setLocationRelativeTo(null);
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
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(query), 0, 1));
        }
    }
    
    private void sesuaikanLebarKolom() {
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
    }
    
    private void loadDataKeTabel() {
        DefaultTableModel tblModel = (DefaultTableModel) jTable1.getModel();
        tblModel.setRowCount(0); 
        try {
            com.mycompany.brinkmikro.app.dao.NasabahDAO dao = new com.mycompany.brinkmikro.app.dao.NasabahDAO();
            java.util.List<com.mycompany.brinkmikro.app.dao.NasabahDAO.NasabahRecord> listData = dao.getAllNasabah();
            
            for (com.mycompany.brinkmikro.app.dao.NasabahDAO.NasabahRecord r : listData) {
                Object[] baris = {
                    r.nik, 
                    r.namaNasabah, 
                    r.alamat, 
                    r.noTelepon, 
                    r.jenisNasabah, 
                    r.tanggalDaftar
                };
                tblModel.addRow(baris);
            }
            sesuaikanLebarKolom(); 
        } catch (java.sql.SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data nasabah: " + e.getMessage());
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

        btnNav2.setBackground(new java.awt.Color(0, 0, 255));
        btnNav2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNav2.setForeground(new java.awt.Color(255, 255, 255));
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
        jLabel5.setText("Manajemen Data Nasabah");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 10, 500, 25));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("Kelola Data Induk Nasabah");
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
        btnAction1.setText("+ Data Nasabah");
        btnAction1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAction1ActionPerformed(evt);
            }
        });

        btnAction2.setBackground(new java.awt.Color(255, 255, 0));
        btnAction2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAction2.setText("Update Nasabah");
        btnAction2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAction2ActionPerformed(evt);
            }
        });

        btnAction3.setBackground(new java.awt.Color(204, 0, 0));
        btnAction3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAction3.setForeground(new java.awt.Color(255, 255, 255));
        btnAction3.setText("Hapus Nasabah");
        btnAction3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAction3ActionPerformed(evt);
            }
        });

        jLabelSearch.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabelSearch.setText("Cari NIK/Nama:");

        txtSearch.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });

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
                .addContainerGap(642, Short.MAX_VALUE))
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
                "NIK", "Nama Nasabah", "Alamat", "No Telepon", "Jenis Nasabah", "Tanggal Daftar"
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
javax.swing.JPanel panelForm = new javax.swing.JPanel(new java.awt.GridLayout(5, 2, 10, 10));
        javax.swing.JTextField txtNik = new javax.swing.JTextField();
        javax.swing.JTextField txtNama = new javax.swing.JTextField();
        javax.swing.JTextField txtAlamat = new javax.swing.JTextField();
        javax.swing.JTextField txtTelepon = new javax.swing.JTextField();
        javax.swing.JComboBox<String> cmbJenis = new javax.swing.JComboBox<>(new String[]{"UMKM", "Petani"});
        
        panelForm.add(new javax.swing.JLabel("NIK :")); panelForm.add(txtNik);
        panelForm.add(new javax.swing.JLabel("Nama Lengkap :")); panelForm.add(txtNama);
        panelForm.add(new javax.swing.JLabel("Alamat :")); panelForm.add(txtAlamat);
        panelForm.add(new javax.swing.JLabel("No. Telepon :")); panelForm.add(txtTelepon);
        panelForm.add(new javax.swing.JLabel("Jenis Nasabah :")); panelForm.add(cmbJenis);
        
        int result = JOptionPane.showConfirmDialog(this, panelForm, "Tambah Data Nasabah", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String nik = txtNik.getText().trim();
            String nama = txtNama.getText().trim();
            String alamat = txtAlamat.getText().trim();
            String telepon = txtTelepon.getText().trim();
            String jenis = cmbJenis.getSelectedItem().toString();

            if (nik.isEmpty() || nama.isEmpty() || alamat.isEmpty() || telepon.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seluruh field wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!nik.matches("\\d{16}")) {
                JOptionPane.showMessageDialog(this, "NIK harus berupa 16 digit angka!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!telepon.matches("\\d{10,15}")) {
                JOptionPane.showMessageDialog(this, "Format Telepon salah! Wajib berupa angka (10-15 digit).", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (nama.length() > 100 || alamat.length() > 255) {
                JOptionPane.showMessageDialog(this, "Nama (maks 100) atau Alamat (maks 255) terlalu panjang!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                com.mycompany.brinkmikro.app.dao.NasabahDAO dao = new com.mycompany.brinkmikro.app.dao.NasabahDAO();
                boolean sukses = dao.tambahNasabah(nik, nama, alamat, telepon, jenis);
                if (sukses) {
                    JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan!");
                    loadDataKeTabel();
                }
            } catch (Exception e) {
                if (e.getMessage().toLowerCase().contains("duplicate entry")) {
                    JOptionPane.showMessageDialog(this, "NIK sudah terdaftar di sistem!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menambah data:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_btnAction1ActionPerformed

    private void btnAction2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAction2ActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris tabel terlebih dahulu!");
            return;
        }
        
        int modelRow = jTable1.convertRowIndexToModel(selectedRow);
        String currNik = model.getValueAt(modelRow, 0).toString(); 
        String currNama = model.getValueAt(modelRow, 1).toString();
        String currAlamat = model.getValueAt(modelRow, 2).toString();
        String currTelp = model.getValueAt(modelRow, 3).toString();
        String currJenis = model.getValueAt(modelRow, 4).toString();
        
        javax.swing.JPanel panelForm = new javax.swing.JPanel(new java.awt.GridLayout(5, 2, 10, 10));
        javax.swing.JTextField txtNik = new javax.swing.JTextField(currNik);
        txtNik.setEnabled(false);
        javax.swing.JTextField txtNama = new javax.swing.JTextField(currNama);
        javax.swing.JTextField txtAlamat = new javax.swing.JTextField(currAlamat);
        javax.swing.JTextField txtTelepon = new javax.swing.JTextField(currTelp);
        javax.swing.JComboBox<String> cmbJenis = new javax.swing.JComboBox<>(new String[]{"UMKM", "Petani"});
        cmbJenis.setSelectedItem(currJenis);
        
        panelForm.add(new javax.swing.JLabel("NIK :")); panelForm.add(txtNik);
        panelForm.add(new javax.swing.JLabel("Nama Lengkap :")); panelForm.add(txtNama);
        panelForm.add(new javax.swing.JLabel("Alamat :")); panelForm.add(txtAlamat);
        panelForm.add(new javax.swing.JLabel("No. Telepon :")); panelForm.add(txtTelepon);
        panelForm.add(new javax.swing.JLabel("Jenis Nasabah :")); panelForm.add(cmbJenis);
        
        int result = JOptionPane.showConfirmDialog(this, panelForm, "Update Nasabah (NIK: " + currNik + ")", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String namaBaru = txtNama.getText().trim();
            String alamatBaru = txtAlamat.getText().trim();
            String teleponBaru = txtTelepon.getText().trim();
            String jenisBaru = cmbJenis.getSelectedItem().toString();

            if (namaBaru.isEmpty() || alamatBaru.isEmpty() || teleponBaru.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seluruh field data nasabah wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!teleponBaru.matches("\\d{10,15}")) {
                JOptionPane.showMessageDialog(this, "Format Telepon salah! Wajib berupa angka (10-15 digit).", "Peringatan Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (namaBaru.length() > 100 || alamatBaru.length() > 255) {
                JOptionPane.showMessageDialog(this, "Nama (maks 100) atau Alamat (maks 255) terlalu panjang!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                com.mycompany.brinkmikro.app.dao.NasabahDAO dao = new com.mycompany.brinkmikro.app.dao.NasabahDAO();
                boolean sukses = dao.updateNasabah(currNik, namaBaru, alamatBaru, teleponBaru, jenisBaru);
                if (sukses) {
                    JOptionPane.showMessageDialog(this, "Data berhasil diperbarui!");
                    loadDataKeTabel();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal update data:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnAction2ActionPerformed

    private void btnAction3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAction3ActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris pada tabel yang ingin dihapus terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus nasabah ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int modelRow = jTable1.convertRowIndexToModel(selectedRow);
                String currNik = model.getValueAt(modelRow, 0).toString();
                
                com.mycompany.brinkmikro.app.dao.NasabahDAO dao = new com.mycompany.brinkmikro.app.dao.NasabahDAO();
                boolean sukses = dao.hapusNasabah(currNik);
                
                if (sukses) {
                    JOptionPane.showMessageDialog(this, "Data berhasil dihapus dari tabel!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    loadDataKeTabel();
                }
            } catch (Exception e) {
                if (e.getMessage().toLowerCase().contains("foreign key") || e.getMessage().toLowerCase().contains("parent row")) {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus: Nasabah masih memiliki riwayat transaksi atau pembiayaan yang terikat di sistem!", "Tolak Hapus Data", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(AdminDataNasabah.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminDataNasabah().setVisible(true);
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
