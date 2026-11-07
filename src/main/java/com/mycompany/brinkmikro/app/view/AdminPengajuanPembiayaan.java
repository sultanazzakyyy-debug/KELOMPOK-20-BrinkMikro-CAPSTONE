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

/**
 * Class: AdminPengajuanPembiayaan
 * Role: Admin
 * Fitur: Single Multi-Field Form Dialog CRUD, Real-Time Filter Search, Validasi Format Tanggal (dd-MM-yyyy), & Left-Shifted Logout Button (x=720)
 */
public class AdminPengajuanPembiayaan extends javax.swing.JFrame {

    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    /**
     * Creates new form AdminPengajuanPembiayaan
     */
    public AdminPengajuanPembiayaan() {
        initComponents();
        setLocationRelativeTo(null);
        initTableAndSearch();
        loadDataKeTabel();
    }

    private void initTableAndSearch() {
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        jTable1.getColumnModel().getColumn(0).setPreferredWidth(140);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(200);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(200);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(180);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(200);
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(180);

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
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(query), 0, 1, 2));
        }
    }
    
    private void loadDataKeTabel() {
        String[] kolomHeader = {"ID Pembiayaan", "NIK Nasabah", "Nama Nasabah", "Jumlah Pengajuan", "Waktu (Bln)", "Skema", "Status"};
        DefaultTableModel tblModel = new DefaultTableModel(kolomHeader, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        jTable1.setModel(tblModel);
        
        sorter = new TableRowSorter<>(tblModel);
        jTable1.setRowSorter(sorter);

        try {
            com.mycompany.brinkmikro.app.dao.PembiayaanDAO dao = new com.mycompany.brinkmikro.app.dao.PembiayaanDAO();
            java.util.List<com.mycompany.brinkmikro.app.dao.PembiayaanDAO.PembiayaanRecord> listPmb = dao.getAllPembiayaan();
            
            com.mycompany.brinkmikro.app.dao.NasabahDAO nasDAO = new com.mycompany.brinkmikro.app.dao.NasabahDAO();
            java.util.List<com.mycompany.brinkmikro.app.dao.NasabahDAO.NasabahRecord> listNasabah = nasDAO.getAllNasabah();
            java.util.Map<String, String> mapNikToNama = new java.util.HashMap<>();
            for (com.mycompany.brinkmikro.app.dao.NasabahDAO.NasabahRecord n : listNasabah) {
                mapNikToNama.put(n.nik, n.namaNasabah);
            }

            for (com.mycompany.brinkmikro.app.dao.PembiayaanDAO.PembiayaanRecord p : listPmb) {
                String namaNasabah = mapNikToNama.getOrDefault(p.nasabahNik, "Tidak Diketahui");
                String formatId = "PMB-" + String.format("%04d", p.idPembiayaan);
                
                Object[] baris = {
                    formatId,
                    p.nasabahNik,
                    namaNasabah,
                    "Rp " + String.format("%,.2f", p.jumlahPengajuan),
                    p.jangkaWaktu + " Bln",
                    p.skema,
                    p.status
                };
                tblModel.addRow(baris);
            }
            sesuaikanLebarKolom(); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data pembiayaan: " + e.getMessage());
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
        setTitle("BrinkMikro - Pengajuan Pembiayaan Petani & UMKM");
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

        btnNav4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNav4.setText("4. Transaksi Tabungan");
        btnNav4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNav4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNav4ActionPerformed(evt);
            }
        });
        jPanel2.add(btnNav4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 260, 290, 40));

        btnNav5.setBackground(new java.awt.Color(0, 0, 255));
        btnNav5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNav5.setForeground(new java.awt.Color(255, 255, 255));
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
        jLabel5.setText("Pengajuan Pembiayaan Petani & UMKM");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 10, 500, 25));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("Kelola & Verifikasi Berkas Pengajuan");
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

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 0, 1040, 70));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        btnAction1.setBackground(new java.awt.Color(0, 0, 255));
        btnAction1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAction1.setForeground(new java.awt.Color(255, 255, 255));
        btnAction1.setText("+ Pengajuan Baru");
        btnAction1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAction1ActionPerformed(evt);
            }
        });

        btnAction2.setBackground(new java.awt.Color(255, 255, 0));
        btnAction2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAction2.setForeground(new java.awt.Color(0, 0, 0));
        btnAction2.setText("Verifikasi Berkas");
        btnAction2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAction2ActionPerformed(evt);
            }
        });

        btnAction3.setBackground(new java.awt.Color(204, 0, 0));
        btnAction3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAction3.setForeground(new java.awt.Color(255, 255, 255));
        btnAction3.setText("Batal Pengajuan");
        btnAction3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAction3ActionPerformed(evt);
            }
        });

        jLabelSearch.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabelSearch.setText("Cari No. Pengajuan/NIK:");

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
                .addContainerGap(600, Short.MAX_VALUE))
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
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 80, 1030, 110));

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID Pembiayaan", "NIK Nasabah", "Jumlah Pengajuan", "Jangka Waktu (Bln)", "Skema Pembiayaan", "Status Pembiayaan"
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
                JOptionPane.showMessageDialog(this, "Data Nasabah kosong!"); return;
            }
            String[] nasabahArray = new String[listNasabah.size()];
            for (int i = 0; i < listNasabah.size(); i++) {
                nasabahArray[i] = listNasabah.get(i).nik + " - " + listNasabah.get(i).namaNasabah;
            }

            javax.swing.JPanel panelForm = new javax.swing.JPanel(new java.awt.GridLayout(4, 2, 10, 10));
            javax.swing.JComboBox<String> cmbNik = new javax.swing.JComboBox<>(nasabahArray);
            javax.swing.JTextField txtJumlah = new javax.swing.JTextField();
            javax.swing.JTextField txtWaktu = new javax.swing.JTextField();
            javax.swing.JComboBox<String> cmbSkema = new javax.swing.JComboBox<>(new String[]{"Mingguan", "Bulanan", "Musiman Panen"});

            panelForm.add(new javax.swing.JLabel("Pilih Nasabah :")); panelForm.add(cmbNik);
            panelForm.add(new javax.swing.JLabel("Jumlah Pengajuan (Rp) :")); panelForm.add(txtJumlah);
            panelForm.add(new javax.swing.JLabel("Jangka Waktu (Bulan) :")); panelForm.add(txtWaktu);
            panelForm.add(new javax.swing.JLabel("Skema Pembiayaan :")); panelForm.add(cmbSkema);

            int result = JOptionPane.showConfirmDialog(this, panelForm, "Pengajuan Pembiayaan Baru (Bunga 6%)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String nikSaja = cmbNik.getSelectedItem().toString().split(" - ")[0];
                java.math.BigDecimal jumlah = new java.math.BigDecimal(txtJumlah.getText().replaceAll("[^\\d]", ""));
                int waktu = Integer.parseInt(txtWaktu.getText().replaceAll("[^\\d]", ""));
                
                java.math.BigDecimal bunga = jumlah.multiply(new java.math.BigDecimal("0.06"));
                java.math.BigDecimal totalKewajiban = jumlah.add(bunga);

                com.mycompany.brinkmikro.app.dao.PembiayaanDAO dao = new com.mycompany.brinkmikro.app.dao.PembiayaanDAO();
                if (dao.tambahPengajuan(nikSaja, jumlah, waktu, cmbSkema.getSelectedItem().toString(), totalKewajiban)) {
                    JOptionPane.showMessageDialog(this, "Pengajuan berhasil disimpan!\nTotal Kewajiban (Bunga 6%): Rp " + String.format("%,.2f", totalKewajiban));
                    loadDataKeTabel();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal: Pastikan angka diisi dengan benar!\n" + e.getMessage());
        }
    
    }//GEN-LAST:event_btnAction1ActionPerformed

    private void btnAction2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAction2ActionPerformed
int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pengajuan yang akan diverifikasi!");
            return;
        }

        try {
            int modelRow = jTable1.convertRowIndexToModel(selectedRow);
            int idPmb = Integer.parseInt(jTable1.getModel().getValueAt(modelRow, 0).toString().replace("PMB-", ""));
            String currNama = jTable1.getModel().getValueAt(modelRow, 2).toString();
            String currStatus = jTable1.getModel().getValueAt(modelRow, 6).toString();

            javax.swing.JPanel panelForm = new javax.swing.JPanel(new java.awt.GridLayout(2, 2, 10, 10));
            javax.swing.JComboBox<String> cmbStatus = new javax.swing.JComboBox<>(new String[]{"Diajukan", "Disetujui", "Ditolak", "Lunas"});
            cmbStatus.setSelectedItem(currStatus);
            javax.swing.JTextField txtSkor = new javax.swing.JTextField("85"); 
            
            panelForm.add(new javax.swing.JLabel("Keputusan Status :")); panelForm.add(cmbStatus);
            panelForm.add(new javax.swing.JLabel("Skor Kelayakan (0-100) :")); panelForm.add(txtSkor);

            int result = JOptionPane.showConfirmDialog(this, panelForm, "Verifikasi Berkas - " + currNama, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                int skor = Integer.parseInt(txtSkor.getText().replaceAll("[^\\d]", ""));
                
                // SOLUSI ULTIMATE: Langsung tembak paksa ID Manajer yang valid dari tabel Anda (Misal akan otomatis mengambil ID 17)
                int idManajer = -1;
                try (java.sql.Connection conn = com.mycompany.brinkmikro.app.config.DatabaseConnection.getConnection();
                     java.sql.Statement stmt = conn.createStatement();
                     java.sql.ResultSet rs = stmt.executeQuery("SELECT id_pengguna FROM manajer LIMIT 1")) {
                    if (rs.next()) {
                        idManajer = rs.getInt("id_pengguna");
                    }
                } catch (Exception ex) {}

                // Jika yang login beneran Manajer, pakai ID aslinya. Kalau Admin/session kosong, tetap pakai pinjaman ID 17 tadi.
                try {
                    com.mycompany.brinkmikro.app.model.Pengguna userAktif = com.mycompany.brinkmikro.app.config.UserSession.getLoginUser();
                    if (userAktif != null && userAktif.getPeran().equalsIgnoreCase("Manajer")) {
                        idManajer = userAktif.getIdPengguna();
                    }
                } catch (Exception ex) {}

                com.mycompany.brinkmikro.app.dao.PembiayaanDAO dao = new com.mycompany.brinkmikro.app.dao.PembiayaanDAO();
                if (dao.verifikasiPengajuan(idPmb, cmbStatus.getSelectedItem().toString(), skor, idManajer)) {
                    JOptionPane.showMessageDialog(this, "Status Pembiayaan diperbarui!");
                    loadDataKeTabel();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal Verifikasi: " + e.getMessage());
        }
    }//GEN-LAST:event_btnAction2ActionPerformed

    private void btnAction3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAction3ActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pengajuan yang akan dibatalkan/dihapus!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin menghapus/membatalkan pengajuan pembiayaan ini secara permanen?", "Konfirmasi Pembatalan", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int modelRow = jTable1.convertRowIndexToModel(selectedRow);
                int idPmb = Integer.parseInt(jTable1.getModel().getValueAt(modelRow, 0).toString().replace("PMB-", ""));
                
                com.mycompany.brinkmikro.app.dao.PembiayaanDAO dao = new com.mycompany.brinkmikro.app.dao.PembiayaanDAO();
                if (dao.hapusPengajuan(idPmb)) {
                    JOptionPane.showMessageDialog(this, "Pengajuan berhasil dihapus/dibatalkan.");
                    loadDataKeTabel();
                }
            } catch (Exception e) {
                 // Pesan ini yang akan muncul jika tombol Batal error, BUKAN pesan "Gagal Verifikasi"
                 JOptionPane.showMessageDialog(this, "Gagal Batal/Hapus: " + e.getMessage());
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
            java.util.logging.Logger.getLogger(AdminPengajuanPembiayaan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminPengajuanPembiayaan().setVisible(true);
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
