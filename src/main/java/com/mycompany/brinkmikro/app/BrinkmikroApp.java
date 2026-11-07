/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.brinkmikro.app;

/**
 *
 * @author ASUS
 */ 

import com.mycompany.brinkmikro.app.view.FormLogin;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class BrinkmikroApp {

    public static void main(String[] args) {
        // Terapkan tema Nimbus bawaan Java agar tampilan lebih rapi
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            System.err.println("Gagal memuat tema: " + ex.getMessage());
        }

        // Panggil dan tampilkan Form Login
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FormLogin().setVisible(true);
            }
        });
    }
}