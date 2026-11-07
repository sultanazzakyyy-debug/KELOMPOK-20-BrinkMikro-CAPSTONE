/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.brinkmikro.app.dao;

/**
 *
 * @author ASUS
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.mycompany.brinkmikro.app.config.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

public class PenggunaDAO {

    public static class PenggunaRecord {
        public int idPengguna;
        public String namaLengkap;
        public String username;
        public String password;
        public String peran;
        public String statusAktif;
    }

    public List<PenggunaRecord> getAllPengguna() throws SQLException {
        List<PenggunaRecord> listData = new ArrayList<>();
        String sql = "SELECT id_pengguna, nama_lengkap, username, password, peran, status_aktif FROM pengguna ORDER BY id_pengguna ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                PenggunaRecord r = new PenggunaRecord();
                r.idPengguna = rs.getInt("id_pengguna");
                r.namaLengkap = rs.getString("nama_lengkap");
                r.username = rs.getString("username");
                r.password = rs.getString("password");
                r.peran = rs.getString("peran");
                r.statusAktif = rs.getString("status_aktif");
                listData.add(r);
            }
        }
        return listData;
    }

    public boolean tambahPengguna(String namaLengkap, String username, String password, String peran, String loket, String cakupanTugas, String batasPersetujuan) throws SQLException {
        if (password == null || password.trim().length() < 8) {
            throw new SQLException("Password minimal 8 karakter");
        }
        
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        
        String sql = "INSERT INTO pengguna (nama_lengkap, username, password, peran, status_aktif) VALUES (?, ?, ?, ?, 'Aktif')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, namaLengkap);
            ps.setString(2, username);
            ps.setString(3, hashedPassword); 
            ps.setString(4, peran);
            
            return ps.executeUpdate() > 0;
        }
    }
    
    public boolean updatePenggunaTanpaPassword(int idPengguna, String namaLengkap, String username, String peran, String statusAktif) throws SQLException {
        String sql = "UPDATE pengguna SET nama_lengkap = ?, username = ?, peran = ?, status_aktif = ? WHERE id_pengguna = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, namaLengkap);
            ps.setString(2, username);
            ps.setString(3, peran);
            ps.setString(4, statusAktif);
            ps.setInt(5, idPengguna);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePenggunaLengkap(int idPengguna, String namaLengkap, String username, String password, String peran, String statusAktif) throws SQLException {
        if (password == null || password.trim().length() < 8) {
            throw new SQLException("Password minimal 8 karakter");
        }
        
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql = "UPDATE pengguna SET nama_lengkap = ?, username = ?, password = ?, peran = ?, status_aktif = ? WHERE id_pengguna = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, namaLengkap);
            ps.setString(2, username);
            ps.setString(3, hashedPassword);
            ps.setString(4, peran);
            ps.setString(5, statusAktif);
            ps.setInt(6, idPengguna);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean hapusPengguna(int idPengguna) throws SQLException {
        String sql = "DELETE FROM pengguna WHERE id_pengguna = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPengguna);
            return ps.executeUpdate() > 0;
        }
    }

    public PenggunaRecord cariUntukLogin(String username, String peran) throws SQLException {
        String sql = "SELECT id_pengguna, nama_lengkap, username, password, peran, status_aktif "
                   + "FROM pengguna WHERE username = ? AND peran = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, peran);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                PenggunaRecord r = new PenggunaRecord();
                r.idPengguna = rs.getInt("id_pengguna");
                r.namaLengkap = rs.getString("nama_lengkap");
                r.username = rs.getString("username");
                r.password = rs.getString("password");
                r.peran = rs.getString("peran");
                r.statusAktif = rs.getString("status_aktif");
                return r;
            }
        }
    }

    public boolean nonaktifkanAkun(int idPengguna) throws SQLException {
        String sql = "UPDATE pengguna SET status_aktif = 'Nonaktif' WHERE id_pengguna = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPengguna);
            return ps.executeUpdate() > 0;
        }
    }
}