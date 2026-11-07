package com.mycompany.brinkmikro.app.dao;

import com.mycompany.brinkmikro.app.config.DatabaseConnection;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardKasDAO {

    public static class ArusKasHarian {
        public String tanggal;
        public BigDecimal setorTabungan;
        public BigDecimal angsuranMasuk;
        public BigDecimal tarikTabungan;
        public BigDecimal pembiayaanCair;
        public BigDecimal netCashFlow;
    }

    public static class SummaryKas {
        public BigDecimal totalSaldoTabungan;
        public BigDecimal totalKasMasukHariIni;
        public BigDecimal totalKasKeluarHariIni;
        public BigDecimal posisiKasLikuiditas;
    }

    public List<ArusKasHarian> getRekapArusKasHarian() throws SQLException {
        List<ArusKasHarian> list = new ArrayList<>();
        
        String sql = "SELECT " +
                     "    tgl, " +
                     "    SUM(setor) AS total_setor, " +
                     "    SUM(angsuran) AS total_angsuran, " +
                     "    SUM(tarik) AS total_tarik, " +
                     "    SUM(cair) AS total_cair " +
                     "FROM ( " +
                     "    SELECT DATE(tanggal_transaksi) AS tgl, jumlah AS setor, 0 AS tarik, 0 AS angsuran, 0 AS cair " +
                     "    FROM transaksi_tabungan WHERE jenis_transaksi = 'Setor Tunai' " +
                     "    UNION ALL " +
                     "    SELECT DATE(tanggal_transaksi) AS tgl, 0 AS setor, jumlah AS tarik, 0 AS angsuran, 0 AS cair " +
                     "    FROM transaksi_tabungan WHERE jenis_transaksi = 'Tarik Tunai' " +
                     "    UNION ALL " +
                     "    SELECT DATE(tanggal_bayar) AS tgl, 0 AS setor, 0 AS tarik, (jumlah_bayar + denda) AS angsuran, 0 AS cair " +
                     "    FROM angsuran " +
                     "    UNION ALL " +
                     "    SELECT DATE(COALESCE(tanggal_persetujuan, tanggal_pengajuan)) AS tgl, 0 AS setor, 0 AS tarik, 0 AS angsuran, jumlah_pengajuan AS cair " +
                     "    FROM pembiayaan WHERE status_pembiayaan IN ('Disetujui', 'Lunas') " +
                     ") gabungan " +
                     "GROUP BY tgl " +
                     "ORDER BY tgl DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ArusKasHarian data = new ArusKasHarian();
                data.tanggal = rs.getString("tgl");
                data.setorTabungan = rs.getBigDecimal("total_setor");
                data.angsuranMasuk = rs.getBigDecimal("total_angsuran");
                data.tarikTabungan = rs.getBigDecimal("total_tarik");
                data.pembiayaanCair = rs.getBigDecimal("total_cair");
                
                BigDecimal totalIn = data.setorTabungan.add(data.angsuranMasuk);
                BigDecimal totalOut = data.tarikTabungan.add(data.pembiayaanCair);
                data.netCashFlow = totalIn.subtract(totalOut);

                list.add(data);
            }
        }
        return list;
    }

    public SummaryKas getSummaryKas() throws SQLException {
        SummaryKas summary = new SummaryKas();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery("SELECT COALESCE(SUM(saldo), 0) AS total FROM rekening_tabungan WHERE status_rekening = 'Aktif'")) {
                summary.totalSaldoTabungan = rs.next() ? rs.getBigDecimal("total") : BigDecimal.ZERO;
            }

            String sqlInHariIni = "SELECT " +
                "(SELECT COALESCE(SUM(jumlah), 0) FROM transaksi_tabungan WHERE jenis_transaksi = 'Setor Tunai' AND DATE(tanggal_transaksi) = CURDATE()) + " +
                "(SELECT COALESCE(SUM(jumlah_bayar + denda), 0) FROM angsuran WHERE DATE(tanggal_bayar) = CURDATE()) AS in_today";
            try (ResultSet rs = stmt.executeQuery(sqlInHariIni)) {
                summary.totalKasMasukHariIni = rs.next() ? rs.getBigDecimal("in_today") : BigDecimal.ZERO;
            }

            String sqlOutHariIni = "SELECT COALESCE(SUM(jumlah), 0) AS out_today FROM transaksi_tabungan WHERE jenis_transaksi = 'Tarik Tunai' AND DATE(tanggal_transaksi) = CURDATE()";
            try (ResultSet rs = stmt.executeQuery(sqlOutHariIni)) {
                summary.totalKasKeluarHariIni = rs.next() ? rs.getBigDecimal("out_today") : BigDecimal.ZERO;
            }

            summary.posisiKasLikuiditas = summary.totalSaldoTabungan.add(summary.totalKasMasukHariIni).subtract(summary.totalKasKeluarHariIni);
        }
        return summary;
    }
}