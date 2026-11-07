package com.mycompany.brinkmikro.app.view;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.mycompany.brinkmikro.app.config.DatabaseConnection;
import com.mycompany.brinkmikro.app.config.UserSession;
import com.mycompany.brinkmikro.app.model.Admin;
import com.mycompany.brinkmikro.app.model.Manajer;
import com.mycompany.brinkmikro.app.model.Pengguna;
import com.mycompany.brinkmikro.app.model.Teller;
import org.mindrot.jbcrypt.BCrypt;

public class FormLogin extends javax.swing.JFrame {
    private int loginAttempts = 0;
    private String lastAttemptedUsername = "";
    
    public FormLogin() {
        initComponents();
        setLocationRelativeTo(null);
        cmbRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Admin", "Teller", "Manajer" }));
        
        jButton2.addActionListener(evt -> {
            txtUsername.setText("");
            txtPassword.setText("");
            cmbRole.setSelectedIndex(0);
            txtUsername.requestFocus();
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanelCard = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        chkShowPassword = new javax.swing.JCheckBox();
        lblRole = new javax.swing.JLabel();
        cmbRole = new javax.swing.JComboBox<>();
        btnLogin = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        lblFooter = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BrinkMikro - Login System");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(235, 240, 245));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanelCard.setBackground(new java.awt.Color(255, 255, 255));
        jPanelCard.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(221, 221, 221), 1, true));
        jPanelCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(51, 0, 255));
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("BrinkMikro");
        jPanelCard.add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 380, 40));

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(102, 102, 102));
        lblSubtitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSubtitle.setText("Sistem Informasi Bank & Keuangan Mikro");
        jPanelCard.add(lblSubtitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 380, 20));

        lblUsername.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblUsername.setForeground(new java.awt.Color(51, 51, 51));
        lblUsername.setText("Username / ID Teller");
        jPanelCard.add(lblUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 210, 360, 20));

        txtUsername.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jPanelCard.add(txtUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 230, 360, 38));

        lblPassword.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblPassword.setForeground(new java.awt.Color(51, 51, 51));
        lblPassword.setText("Password");
        jPanelCard.add(lblPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 280, 360, 20));

        txtPassword.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPasswordActionPerformed(evt);
            }
        });
        jPanelCard.add(txtPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 310, 360, 38));

        chkShowPassword.setBackground(new java.awt.Color(255, 255, 255));
        chkShowPassword.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        chkShowPassword.setForeground(new java.awt.Color(51, 51, 51));
        chkShowPassword.setText("Tampilkan Password");
        chkShowPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowPasswordActionPerformed(evt);
            }
        });
        jPanelCard.add(chkShowPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 350, 160, 22));

        lblRole.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblRole.setForeground(new java.awt.Color(51, 51, 51));
        lblRole.setText("Hak Akses (Role)");
        jPanelCard.add(lblRole, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 380, 360, 20));

        cmbRole.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jPanelCard.add(cmbRole, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 410, 360, 38));

        btnLogin.setBackground(new java.awt.Color(51, 0, 255));
        btnLogin.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnLogin.setForeground(new java.awt.Color(255, 255, 255));
        btnLogin.setText("LOGIN SEKARANG");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        jPanelCard.add(btnLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 470, 220, 42));

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setText("RESET");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanelCard.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 470, 130, 42));

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/mycompany/brinkmikro/app/assets/logo_1.jpeg"))); // NOI18N
        jPanelCard.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 40, 70, 70));

        jPanel1.add(jPanelCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(463, 74, 440, 560));

        lblFooter.setForeground(new java.awt.Color(119, 119, 119));
        lblFooter.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFooter.setText("© 2026 BrinkMikro Financial System. All rights reserved.");
        jPanel1.add(lblFooter, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 660, 1366, 30));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1370, 790));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void chkShowPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowPasswordActionPerformed
        if (chkShowPassword.isSelected()) {
            txtPassword.setEchoChar((char) 0);
        } else {
            txtPassword.setEchoChar('•');
        }
    }//GEN-LAST:event_chkShowPasswordActionPerformed

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String selectedRole = (String) cmbRole.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.length() < 8) {
            JOptionPane.showMessageDialog(this, "Password minimal 8 karakter.", "Peringatan Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!username.equals(lastAttemptedUsername)) {
            loginAttempts = 0;
            lastAttemptedUsername = username;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id_pengguna, nama_lengkap, password, status_aktif FROM pengguna WHERE username = ? AND peran = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, selectedRole);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int idPengguna = rs.getInt("id_pengguna");
                        String namaLengkap = rs.getString("nama_lengkap");
                        String dbPassword = rs.getString("password");
                        String statusAktif = rs.getString("status_aktif");

                        if (!"Aktif".equalsIgnoreCase(statusAktif)) {
                            JOptionPane.showMessageDialog(this, "Akun berstatus: " + statusAktif, "Akun Terkunci", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        boolean isPasswordMatch = false;
                        if (dbPassword.startsWith("$2a$") || dbPassword.startsWith("$2y$") || dbPassword.startsWith("$2b$")) {
                            isPasswordMatch = BCrypt.checkpw(password, dbPassword);
                        } else {
                            isPasswordMatch = password.equals(dbPassword);
                        }

                        if (isPasswordMatch) {
                            loginAttempts = 0; 
                            Pengguna userAktif = null;
                            if (selectedRole.equalsIgnoreCase("Admin")) {
                                userAktif = new Admin(idPengguna, namaLengkap, username, dbPassword, statusAktif, "", "");
                            } else if (selectedRole.equalsIgnoreCase("Teller")) {
                                userAktif = new Teller(idPengguna, namaLengkap, username, dbPassword, statusAktif, "", "");
                            } else if (selectedRole.equalsIgnoreCase("Manajer")) {
                                userAktif = new Manajer(idPengguna, namaLengkap, username, dbPassword, statusAktif, null, "");
                            }
                            UserSession.setLoginUser(userAktif);
                            this.dispose();

                            if (selectedRole.equalsIgnoreCase("Admin")) {
                                new AdminDataPengguna().setVisible(true); 
                            }
                        } else {
                            loginAttempts++;
                            int sisaPercobaan = 3 - loginAttempts;
                            if (loginAttempts >= 3) {
                                String updateSql = "UPDATE pengguna SET status_aktif = 'Nonaktif' WHERE id_pengguna = ?";
                                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                                    updatePs.setInt(1, idPengguna);
                                    updatePs.executeUpdate();
                                }
                                JOptionPane.showMessageDialog(this, "Gagal login 3 kali. Akun DINONAKTIFKAN.", "Keamanan Sistem", JOptionPane.ERROR_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(this, "Password salah! Sisa percobaan: " + sisaPercobaan, "Gagal Login", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Username atau Hak Akses tidak sesuai!", "Gagal Login", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Koneksi Database Bermasalah:\n" + e.getMessage(), "Error Sistem", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnLoginActionPerformed

    private void txtPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasswordActionPerformed

    }//GEN-LAST:event_txtPasswordActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(FormLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormLogin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JCheckBox chkShowPassword;
    private javax.swing.JComboBox<String> cmbRole;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelCard;
    private javax.swing.JLabel lblFooter;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblRole;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
