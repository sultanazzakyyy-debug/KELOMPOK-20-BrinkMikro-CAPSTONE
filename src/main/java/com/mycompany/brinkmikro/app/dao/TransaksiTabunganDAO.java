package com.mycompany.brinkmikro.app.dao;

import com.mycompany.brinkmikro.app.config.DatabaseConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TransaksiTabunganDAO {

    public static class TransaksiRecord {
        public int idTransaksi;
        public Timestamp tanggal;
        public String jenis;
        public BigDecimal jumlah;
        public String nomorRekening;
        public int tellerId;
    }

    /** Semua riwayat transaksi, terbaru di atas. */
    public List<TransaksiRecord> getAllTransaksi() throws SQLException {
        List<TransaksiRecord> list = new ArrayList<>();
        String sql = "SELECT id_transaksi, tanggal_transaksi, jenis_transaksi, jumlah, "
                   + "nomor_rekening, teller_id_pengguna FROM transaksi_tabungan "
                   + "ORDER BY id_transaksi DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TransaksiRecord r = new TransaksiRecord();
                r.idTransaksi = rs.getInt("id_transaksi");
                r.tanggal = rs.getTimestamp("tanggal_transaksi");
                r.jenis = rs.getString("jenis_transaksi");
                r.jumlah = rs.getBigDecimal("jumlah");
                r.nomorRekening = rs.getString("nomor_rekening");
                r.tellerId = rs.getInt("teller_id_pengguna");
                list.add(r);
            }
        }
        return list;
    }

    /** Daftar teller aktif untuk dropdown: {id, nama}. */
    public List<String[]> getDaftarTeller() throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT id_pengguna, nama_lengkap FROM pengguna "
                   + "WHERE peran = 'Teller' AND status_aktif = 'Aktif' ORDER BY nama_lengkap";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{String.valueOf(rs.getInt("id_pengguna")), rs.getString("nama_lengkap")});
            }
        }
        return list;
    }

    /**
     * Setor / tarik dalam SATU database transaction:
     * kunci baris rekening -> validasi -> insert transaksi -> update saldo -> commit.
     * Jika salah satu gagal, semuanya di-rollback.
     *
     * @return saldo terbaru
     */
    public BigDecimal prosesTransaksi(String nomorRekening, String jenis, BigDecimal jumlah, int tellerId)
            throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                BigDecimal saldo;
                String status;
                String lockSql = "SELECT saldo, status_rekening FROM rekening_tabungan "
                               + "WHERE nomor_rekening = ? FOR UPDATE";
                try (PreparedStatement ps = conn.prepareStatement(lockSql)) {
                    ps.setString(1, nomorRekening);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalArgumentException("Rekening tidak ditemukan.");
                        }
                        saldo = rs.getBigDecimal("saldo");
                        status = rs.getString("status_rekening");
                    }
                }

                if (!"Aktif".equalsIgnoreCase(status)) {
                    throw new IllegalArgumentException("Rekening berstatus '" + status + "', transaksi ditolak.");
                }

                boolean setor = "Setor Tunai".equals(jenis);
                if (!setor && saldo.compareTo(jumlah) < 0) {
                    throw new IllegalArgumentException("Saldo tidak mencukupi.");
                }
                BigDecimal saldoBaru = setor ? saldo.add(jumlah) : saldo.subtract(jumlah);

                String insSql = "INSERT INTO transaksi_tabungan "
                              + "(jenis_transaksi, jumlah, nomor_rekening, teller_id_pengguna) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insSql)) {
                    ps.setString(1, jenis);
                    ps.setBigDecimal(2, jumlah);
                    ps.setString(3, nomorRekening);
                    ps.setInt(4, tellerId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE rekening_tabungan SET saldo = ? WHERE nomor_rekening = ?")) {
                    ps.setBigDecimal(1, saldoBaru);
                    ps.setString(2, nomorRekening);
                    ps.executeUpdate();
                }

                conn.commit();
                return saldoBaru;
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
