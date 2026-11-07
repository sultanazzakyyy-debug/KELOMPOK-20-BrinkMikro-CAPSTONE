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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NasabahDAO {

    public static class NasabahRecord {
        public String nik;
        public String namaNasabah;
        public String alamat;
        public String noTelepon;
        public String jenisNasabah;
        public String tanggalDaftar;
    }

    public List<NasabahRecord> getAllNasabah() throws SQLException {
        List<NasabahRecord> list = new ArrayList<>();
        String sql = "SELECT nik, nama_nasabah, alamat, no_telepon, jenis_nasabah, tanggal_daftar FROM nasabah ORDER BY tanggal_daftar DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                NasabahRecord r = new NasabahRecord();
                r.nik = rs.getString("nik");
                r.namaNasabah = rs.getString("nama_nasabah");
                r.alamat = rs.getString("alamat");
                r.noTelepon = rs.getString("no_telepon");
                r.jenisNasabah = rs.getString("jenis_nasabah");
                r.tanggalDaftar = rs.getString("tanggal_daftar");
                list.add(r);
            }
        }
        return list;
    }

    public boolean tambahNasabah(String nik, String nama, String alamat, String noTelepon, String jenisNasabah) throws SQLException {
        String sql = "INSERT INTO nasabah (nik, nama_nasabah, alamat, no_telepon, jenis_nasabah, tanggal_daftar) VALUES (?, ?, ?, ?, ?, CURDATE())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nik);
            ps.setString(2, nama);
            ps.setString(3, alamat);
            ps.setString(4, noTelepon);
            ps.setString(5, jenisNasabah);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean tambahNasabahLengkap(String nik, String nama, String alamat, String noTelepon, String jenisNasabah,
                                        String param1, String param2, BigDecimal nominal) throws SQLException {
        String sqlNasabah = "INSERT INTO nasabah (nik, nama_nasabah, alamat, no_telepon, jenis_nasabah, tanggal_daftar) VALUES (?, ?, ?, ?, ?, CURDATE())";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(sqlNasabah)) {
                    ps.setString(1, nik);
                    ps.setString(2, nama);
                    ps.setString(3, alamat);
                    ps.setString(4, noTelepon);
                    ps.setString(5, jenisNasabah);
                    ps.executeUpdate();
                }

                if ("Petani".equalsIgnoreCase(jenisNasabah)) {
                    String sqlPetani = "INSERT INTO petani (nik, luas_lahan, komoditas_utama, pendapatan_tahunan) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement psPetani = conn.prepareStatement(sqlPetani)) {
                        psPetani.setString(1, nik);
                        psPetani.setBigDecimal(2, new BigDecimal(param1 != null && !param1.isEmpty() ? param1 : "0.0"));
                        psPetani.setString(3, param2 != null ? param2 : "-");
                        psPetani.setBigDecimal(4, nominal != null ? nominal : BigDecimal.ZERO);
                        psPetani.executeUpdate();
                    }
                } else if ("UMKM".equalsIgnoreCase(jenisNasabah)) {
                    String sqlUmkm = "INSERT INTO pedagang_umkm (nik, jenis_usaha, lokasi_usaha, omzet_bulanan) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement psUmkm = conn.prepareStatement(sqlUmkm)) {
                        psUmkm.setString(1, nik);
                        psUmkm.setString(2, param1 != null ? param1 : "-");
                        psUmkm.setString(3, param2 != null ? param2 : "-");
                        psUmkm.setBigDecimal(4, nominal != null ? nominal : BigDecimal.ZERO);
                        psUmkm.executeUpdate();
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

public boolean updateNasabah(String nik, String nama, String alamat, String telepon, String jenis) throws SQLException {
        String sql = "UPDATE nasabah SET nama_nasabah = ?, alamat = ?, no_telepon = ?, jenis_nasabah = ? WHERE nik = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, alamat);
            ps.setString(3, telepon);
            ps.setString(4, jenis);
            ps.setString(5, nik);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean hapusNasabah(String nik) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM petani WHERE nik = ?")) {
                    ps1.setString(1, nik);
                    ps1.executeUpdate();
                }
                try (PreparedStatement ps2 = conn.prepareStatement("DELETE FROM pedagang_umkm WHERE nik = ?")) {
                    ps2.setString(1, nik);
                    ps2.executeUpdate();
                }
                try (PreparedStatement ps3 = conn.prepareStatement("DELETE FROM nasabah WHERE nik = ?")) {
                    ps3.setString(1, nik);
                    boolean hasil = ps3.executeUpdate() > 0;
                    conn.commit();
                    return hasil;
                }
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }
}