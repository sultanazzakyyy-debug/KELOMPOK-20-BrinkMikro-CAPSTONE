/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.brinkmikro.app.controller;

import com.mycompany.brinkmikro.app.config.UserSession;
import com.mycompany.brinkmikro.app.model.Pengguna;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
/**
 *
 * @author Adbang 18
 */
public abstract class BaseController {

    public static final String ADMIN = "Admin";
    public static final String TELLER = "Teller";
    public static final String MANAJER = "Manajer";
    public abstract String getNamaModul();

    @FunctionalInterface
    protected interface AksiDb<T> {
        T jalankan() throws SQLException;
    }

    // ------------------------------------------------------------------
    // Eksekusi DAO + penerjemahan error
    // ------------------------------------------------------------------

    protected <T> T eksekusi(AksiDb<T> aksi) throws ControllerException {
        try {
            return aksi.jalankan();
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ControllerException(e.getMessage(), e);
        } catch (SQLException e) {
            throw petakanSql(e);
        }
    }

    protected void pastikanBerhasil(boolean berhasil, String pesanGagal) throws ControllerException {
        if (!berhasil) {
            throw new ControllerException(pesanGagal);
        }
    }

    private ControllerException petakanSql(SQLException e) {
        // SQLException buatan DAO (mis. "Password minimal 8 karakter") tidak punya SQLState.
        if (e.getSQLState() == null && e.getErrorCode() == 0) {
            return new ControllerException(e.getMessage(), e);
        }
        switch (e.getErrorCode()) {
            case 1062:
                return new ControllerException("Data sudah terdaftar (duplikat) pada modul "
                        + getNamaModul() + ".", e);
            case 1451:
                return new ControllerException("Data tidak dapat dihapus atau diubah karena masih "
                        + "dipakai oleh data lain (mis. transaksi, rekening, atau pembiayaan).", e);
            case 1452:
                return new ControllerException("Data rujukan tidak ditemukan "
                        + "(mis. nasabah, rekening, atau pembiayaan yang dipilih tidak valid).", e);
            case 1406:
                return new ControllerException("Salah satu isian terlalu panjang.", e);
            default:
                return new ControllerException("Kesalahan database pada modul "
                        + getNamaModul() + ": " + e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------------
    // Hak akses
    // ------------------------------------------------------------------

    protected Pengguna penggunaAktif() throws ControllerException {
        Pengguna user = UserSession.getLoginUser();
        if (user == null) {
            throw new ControllerException("Sesi login tidak ditemukan. Silakan login kembali.");
        }
        return user;
    }

    protected Pengguna wajibPeran(String... peranDiizinkan) throws ControllerException {
        Pengguna user = penggunaAktif();
        for (String peran : peranDiizinkan) {
            if (peran.equalsIgnoreCase(user.getPeran())) {
                return user;
            }
        }
        throw new ControllerException("Akses ditolak. Fitur ini hanya untuk peran: "
                + String.join(", ", peranDiizinkan) + ".");
    }

    protected boolean peranAdalah(Pengguna user, String peran) {
        return user != null && peran.equalsIgnoreCase(user.getPeran());
    }

    // ------------------------------------------------------------------
    // Validasi & konversi input
    // ------------------------------------------------------------------

    protected String wajib(String nilai, String label) throws ControllerException {
        if (nilai == null || nilai.trim().isEmpty()) {
            throw new ControllerException("Kolom '" + label + "' tidak boleh kosong!");
        }
        return nilai.trim();
    }

    protected String wajib(String nilai, String label, int panjangMaks) throws ControllerException {
        String hasil = wajib(nilai, label);
        if (hasil.length() > panjangMaks) {
            throw new ControllerException("Kolom '" + label + "' maksimal " + panjangMaks + " karakter!");
        }
        return hasil;
    }

    protected String pilihDari(String nilai, String label, String... pilihanSah) throws ControllerException {
        String hasil = wajib(nilai, label);
        for (String sah : pilihanSah) {
            if (sah.equals(hasil)) {
                return sah;
            }
        }
        throw new ControllerException("Nilai '" + label + "' tidak valid. Pilihan: "
                + String.join(", ", pilihanSah) + ".");
    }

    protected boolean termasuk(String nilai, String... pilihan) {
        return Arrays.asList(pilihan).contains(nilai);
    }

    protected int angkaBulat(String nilai, String label) throws ControllerException {
        String digit = digitSaja(wajib(nilai, label));
        if (digit.isEmpty() || digit.length() > 9) {
            throw new ControllerException("Kolom '" + label + "' harus berisi angka yang valid!");
        }
        return Integer.parseInt(digit);
    }

    protected BigDecimal nominal(String nilai, String label) throws ControllerException {
        BigDecimal hasil = nominalBolehNol(nilai, label);
        if (hasil.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ControllerException("Kolom '" + label + "' harus lebih dari Rp 0!");
        }
        return hasil;
    }

    protected BigDecimal nominalBolehNol(String nilai, String label) throws ControllerException {
        if (nilai == null || nilai.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        String digit = digitSaja(nilai);
        if (digit.isEmpty() || digit.length() > 13) {
            throw new ControllerException("Kolom '" + label + "' harus berisi nominal yang valid!");
        }
        return new BigDecimal(digit);
    }

    protected int ambilId(String tampilan, String label) throws ControllerException {
        String bagianAwal = wajib(tampilan, label).split(" - ")[0];
        String digit = digitSaja(bagianAwal);
        if (digit.isEmpty() || digit.length() > 9) {
            throw new ControllerException("ID '" + label + "' tidak valid!");
        }
        return Integer.parseInt(digit);
    }

    protected String ambilKunci(String pilihan, String label) throws ControllerException {
        return wajib(pilihan, label).split(" - ")[0].trim();
    }

    private String digitSaja(String teks) {
        return teks.replaceAll("[^\\d]", "");
    }
}
 