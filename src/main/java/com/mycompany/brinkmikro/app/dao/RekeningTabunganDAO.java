/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.brinkmikro.app.dao;

/**
 *
 * @author ASUS
 */

import com.mycompany.brinkmikro.app.config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RekeningTabunganDAO {

    public static class RekeningRecord {
        public String nomorRekening;
        public double saldo;
        public String tanggalPembukaan;
        public String statusRekening;
        public String nasabahNik;
        public String namaNasabah;
    }

    public List<RekeningRecord> getAllRekening() throws SQLException {
        List<RekeningRecord> list = new ArrayList<>();
        String sql = "SELECT r.nomor_rekening, r.saldo, r.tanggal_pembukaan, r.status_rekening, r.nasabah_nik, n.nama_nasabah " +
                     "FROM rekening_tabungan r " +
                     "JOIN nasabah n ON r.nasabah_nik = n.nik " +
                     "ORDER BY r.tanggal_pembukaan DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                RekeningRecord r = new RekeningRecord();
                r.nomorRekening = rs.getString("nomor_rekening");
                r.saldo = rs.getDouble("saldo");
                r.tanggalPembukaan = rs.getString("tanggal_pembukaan");
                r.statusRekening = rs.getString("status_rekening");
                r.nasabahNik = rs.getString("nasabah_nik");
                r.namaNasabah = rs.getString("nama_nasabah");
                list.add(r);
            }
        }
        return list;
    }

    public boolean tambahRekening(String nomorRekening, String nasabahNik) throws SQLException {
        String sql = "INSERT INTO rekening_tabungan (nomor_rekening, saldo, tanggal_pembukaan, status_rekening, nasabah_nik) " +
                     "VALUES (?, 0.00, CURDATE(), 'Perlu Verifikasi', ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nomorRekening);
            ps.setString(2, nasabahNik);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStatusRekening(String nomorRekening, String statusRekening) throws SQLException {
        String sql = "UPDATE rekening_tabungan SET status_rekening = ? WHERE nomor_rekening = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statusRekening);
            ps.setString(2, nomorRekening);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean hapusRekening(String nomorRekening) throws SQLException {
        String sql = "DELETE FROM rekening_tabungan WHERE nomor_rekening = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nomorRekening);
            return ps.executeUpdate() > 0;
        }
    }
}
