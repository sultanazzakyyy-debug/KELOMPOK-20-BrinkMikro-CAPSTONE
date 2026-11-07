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
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PembiayaanDAO {

    public static class PembiayaanRecord {
        public int idPembiayaan;
        public int jangkaWaktu;
        public int jangkaWaktuBulan;
        public int skorKelayakan;
        public int idManajer;
        public Integer manajerIdPengguna;
        public BigDecimal jumlahPengajuan;
        public BigDecimal sukuBunga;
        public BigDecimal sukuBungaPersen;
        public BigDecimal totalKewajiban;
        public String skema;
        public String skemaPembiayaan;
        public String status;
        public String statusPembiayaan;
        public String nasabahNik;
        public String namaNasabah;
        public String jenisNasabah;
        public String namaManajer;
        public Date tanggalPengajuan;
    }

    public List<PembiayaanRecord> getAllPembiayaan() throws SQLException {
        List<PembiayaanRecord> list = new ArrayList<>();
        String sql = "SELECT p.*, n.nama_nasabah, n.jenis_nasabah, u.nama_lengkap AS nama_manajer " +
                     "FROM pembiayaan p " +
                     "JOIN nasabah n ON p.nasabah_nik = n.nik " +
                     "LEFT JOIN pengguna u ON p.manajer_id_pengguna = u.id_pengguna " +
                     "ORDER BY p.id_pembiayaan DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PembiayaanRecord r = new PembiayaanRecord();
                r.idPembiayaan = rs.getInt("id_pembiayaan");
                r.jumlahPengajuan = rs.getBigDecimal("jumlah_pengajuan");
                r.jangkaWaktuBulan = rs.getInt("jangka_waktu_bulan");
                r.jangkaWaktu = r.jangkaWaktuBulan;
                r.sukuBungaPersen = rs.getBigDecimal("suku_bunga_persen");
                r.sukuBunga = r.sukuBungaPersen;
                r.totalKewajiban = rs.getBigDecimal("total_kewajiban");
                r.skemaPembiayaan = rs.getString("skema_pembiayaan");
                r.skema = r.skemaPembiayaan;
                r.skorKelayakan = rs.getInt("skor_kelayakan");
                r.statusPembiayaan = rs.getString("status_pembiayaan");
                r.status = r.statusPembiayaan;
                r.tanggalPengajuan = rs.getDate("tanggal_pengajuan");
                r.nasabahNik = rs.getString("nasabah_nik");
                r.namaNasabah = rs.getString("nama_nasabah");
                r.jenisNasabah = rs.getString("jenis_nasabah");
                int manajerId = rs.getInt("manajer_id_pengguna");
                r.manajerIdPengguna = rs.wasNull() ? null : manajerId;
                r.idManajer = r.manajerIdPengguna != null ? r.manajerIdPengguna : 0;
                r.namaManajer = rs.getString("nama_manajer");
                list.add(r);
            }
        }
        return list;
    }

    /**
     * Sisa hutang aktif seorang nasabah = total kewajiban semua pembiayaan berstatus
     * 'Disetujui' dikurangi seluruh angsuran yang sudah dibayarkan pada pembiayaan tersebut.
     * Pembiayaan yang sudah 'Lunas', 'Ditolak', atau masih 'Diajukan' tidak dihitung.
     */
    public BigDecimal getSisaHutangNasabah(String nik) throws SQLException {
        String sql = "SELECT "
                   + "COALESCE((SELECT SUM(p.total_kewajiban) FROM pembiayaan p "
                   + "          WHERE p.nasabah_nik = ? AND p.status_pembiayaan = 'Disetujui'), 0) AS total_kewajiban, "
                   + "COALESCE((SELECT SUM(a.jumlah_bayar) FROM angsuran a "
                   + "          JOIN pembiayaan p2 ON a.id_pembiayaan = p2.id_pembiayaan "
                   + "          WHERE p2.nasabah_nik = ? AND p2.status_pembiayaan = 'Disetujui'), 0) AS total_terbayar";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nik);
            ps.setString(2, nik);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return BigDecimal.ZERO;
                }
                BigDecimal sisa = rs.getBigDecimal("total_kewajiban").subtract(rs.getBigDecimal("total_terbayar"));
                return sisa.signum() < 0 ? BigDecimal.ZERO : sisa;
            }
        }
    }

    public BigDecimal hitungTotalKewajiban(BigDecimal jumlahPengajuan, int jangkaWaktuBulan, BigDecimal sukuBungaPersen) {
        BigDecimal bungaProporsional = jumlahPengajuan
                .multiply(sukuBungaPersen.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP))
                .multiply(new BigDecimal(jangkaWaktuBulan))
                .divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
        return jumlahPengajuan.add(bungaProporsional).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean tambahPengajuan(BigDecimal jumlahPengajuan, int jangkaWaktuBulan, BigDecimal sukuBungaPersen,
                                   String skemaPembiayaan, Integer skorKelayakan, String nikNasabah) throws SQLException {
        if (jumlahPengajuan == null || jumlahPengajuan.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Jumlah pengajuan harus lebih dari Rp 0!");
        }
        if (sukuBungaPersen == null) {
            sukuBungaPersen = new BigDecimal("6.00");
        }

        BigDecimal totalKewajiban = hitungTotalKewajiban(jumlahPengajuan, jangkaWaktuBulan, sukuBungaPersen);

        String sql = "INSERT INTO pembiayaan (jumlah_pengajuan, jangka_waktu_bulan, suku_bunga_persen, " +
                     "total_kewajiban, skema_pembiayaan, skor_kelayakan, status_pembiayaan, tanggal_pengajuan, nasabah_nik) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 'Diajukan', CURRENT_DATE, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, jumlahPengajuan);
            ps.setInt(2, jangkaWaktuBulan);
            ps.setBigDecimal(3, sukuBungaPersen);
            ps.setBigDecimal(4, totalKewajiban);
            ps.setString(5, skemaPembiayaan);
            if (skorKelayakan != null) {
                ps.setInt(6, skorKelayakan);
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setString(7, nikNasabah);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean tambahPengajuan(String nik, BigDecimal jumlah, int waktu, String skema, BigDecimal kewajiban) throws SQLException {
        String sql = "INSERT INTO pembiayaan (jumlah_pengajuan, jangka_waktu_bulan, total_kewajiban, skema_pembiayaan, tanggal_pengajuan, nasabah_nik, suku_bunga_persen, status_pembiayaan) VALUES (?, ?, ?, ?, CURDATE(), ?, 6.00, 'Diajukan')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, jumlah);
            ps.setInt(2, waktu);
            ps.setBigDecimal(3, kewajiban);
            ps.setString(4, skema);
            ps.setString(5, nik);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePembiayaan(int idPembiayaan, BigDecimal jumlahPengajuan, int jangkaWaktuBulan,
                                    BigDecimal sukuBungaPersen, String skemaPembiayaan, Integer skorKelayakan) throws SQLException {
        BigDecimal totalKewajiban = hitungTotalKewajiban(jumlahPengajuan, jangkaWaktuBulan, sukuBungaPersen);
        String sql = "UPDATE pembiayaan SET jumlah_pengajuan = ?, jangka_waktu_bulan = ?, suku_bunga_persen = ?, " +
                     "total_kewajiban = ?, skema_pembiayaan = ?, skor_kelayakan = ? WHERE id_pembiayaan = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, jumlahPengajuan);
            ps.setInt(2, jangkaWaktuBulan);
            ps.setBigDecimal(3, sukuBungaPersen);
            ps.setBigDecimal(4, totalKewajiban);
            ps.setString(5, skemaPembiayaan);
            if (skorKelayakan != null) {
                ps.setInt(6, skorKelayakan);
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setInt(7, idPembiayaan);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean putuskanPembiayaan(int idPembiayaan, Integer manajerIdPengguna, boolean disetujui) throws SQLException {
        String statusBaru = disetujui ? "Disetujui" : "Ditolak";

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (disetujui && manajerIdPengguna != null) {
                String cekLimitSql = "SELECT p.jumlah_pengajuan, m.batas_persetujuan " +
                                     "FROM pembiayaan p " +
                                     "LEFT JOIN manajer m ON m.id_pengguna = ? " +
                                     "WHERE p.id_pembiayaan = ?";
                try (PreparedStatement psCek = conn.prepareStatement(cekLimitSql)) {
                    psCek.setInt(1, manajerIdPengguna);
                    psCek.setInt(2, idPembiayaan);
                    try (ResultSet rs = psCek.executeQuery()) {
                        if (rs.next()) {
                            BigDecimal pengajuan = rs.getBigDecimal("jumlah_pengajuan");
                            BigDecimal batas = rs.getBigDecimal("batas_persetujuan");
                            if (batas != null && pengajuan.compareTo(batas) > 0) {
                                throw new IllegalStateException("Jumlah pengajuan melebihi batas persetujuan Manajer (Rp " + batas + ")!");
                            }
                        }
                    }
                }
            }

            String sql = "UPDATE pembiayaan SET status_pembiayaan = ?, manajer_id_pengguna = ? WHERE id_pembiayaan = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, statusBaru);
                if (manajerIdPengguna != null) {
                    ps.setInt(2, manajerIdPengguna);
                } else {
                    ps.setNull(2, Types.INTEGER);
                }
                ps.setInt(3, idPembiayaan);
                return ps.executeUpdate() > 0;
            }
        }
    }

    public boolean verifikasiPengajuan(int idPembiayaan, String status, int skor, int idManajer) throws SQLException {
        String sql = "UPDATE pembiayaan SET status_pembiayaan = ?, skor_kelayakan = ?, manajer_id_pengguna = ? WHERE id_pembiayaan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, skor);
            if (idManajer > 0) {
                ps.setInt(3, idManajer);
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setInt(4, idPembiayaan);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean hapusPengajuan(int idPembiayaan) throws SQLException {
        String sql = "DELETE FROM pembiayaan WHERE id_pembiayaan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPembiayaan);
            return ps.executeUpdate() > 0;
        }
    }
}
