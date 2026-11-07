/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.brinkmikro.app.controller;

import com.mycompany.brinkmikro.app.dao.NasabahDAO;
import com.mycompany.brinkmikro.app.dao.NasabahDAO.NasabahRecord;
import com.mycompany.brinkmikro.app.dao.PembiayaanDAO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Adbang 18
 */
public class NasabahController extends BaseController {

    public static final String PETANI = "Petani";
    public static final String UMKM = "UMKM";

    private final NasabahDAO dao;
    private final PembiayaanDAO pembiayaanDAO;

    public NasabahController() {
        this(new NasabahDAO(), new PembiayaanDAO());
    }

    public NasabahController(NasabahDAO dao) {
        this(dao, new PembiayaanDAO());
    }

    public NasabahController(NasabahDAO dao, PembiayaanDAO pembiayaanDAO) {
        this.dao = dao;
        this.pembiayaanDAO = pembiayaanDAO;
    }

    @Override
    public String getNamaModul() {
        return "Data Nasabah";
    }

    // ---------------------------- baca ----------------------------

    public List<NasabahRecord> daftar() throws ControllerException {
        penggunaAktif();
        return eksekusi(dao::getAllNasabah);
    }

    // Isi combo box pilihan nasabah dengan format "NIK - Nama".
    public List<String> daftarPilihan() throws ControllerException {
        List<String> hasil = new ArrayList<>();
        for (NasabahRecord n : daftar()) {
            hasil.add(n.nik + " - " + n.namaNasabah);
        }
        return hasil;
    }

    // Mencari nasabah berdasarkan NIK; null bila tidak ada.
    public NasabahRecord cari(String nik) throws ControllerException {
        for (NasabahRecord n : daftar()) {
            if (n.nik.equals(nik)) {
                return n;
            }
        }
        return null;
    }

    // Sisa hutang (pembiayaan Disetujui dikurangi angsuran terbayar) milik seorang nasabah.
    // Boleh dibaca oleh semua peran yang sudah login (termasuk Teller).
    public BigDecimal sisaHutang(String nik) throws ControllerException {
        penggunaAktif();
        String nikOk = validasiNik(nik);
        return eksekusi(() -> pembiayaanDAO.getSisaHutangNasabah(nikOk));
    }

    // --------------------------- tambah ---------------------------

    // Menambah nasabah (data induk saja). 
    public void tambah(String nik, String nama, String alamat, String telepon, String jenis)
            throws ControllerException {
        wajibPeran(ADMIN, TELLER);
        String nikOk = validasiNik(nik);
        String namaOk = wajib(nama, "Nama Nasabah", 100);
        String alamatOk = wajib(alamat, "Alamat", 200);
        String telpOk = validasiTelepon(telepon);
        String jenisOk = pilihDari(jenis, "Jenis Nasabah", PETANI, UMKM);

        pastikanBerhasil(eksekusi(() -> dao.tambahNasabah(nikOk, namaOk, alamatOk, telpOk, jenisOk)),
                "Nasabah gagal ditambahkan.");
    }

    // Menambah nasabah Petani sekaligus detail lahan (satu transaksi database).
    public void tambahPetani(String nik, String nama, String alamat, String telepon,
                             String luasLahan, String komoditasUtama, String pendapatanTahunan)
            throws ControllerException {
        wajibPeran(ADMIN, TELLER);
        String nikOk = validasiNik(nik);
        String namaOk = wajib(nama, "Nama Nasabah", 100);
        String alamatOk = wajib(alamat, "Alamat", 200);
        String telpOk = validasiTelepon(telepon);
        String luasOk = validasiLuasLahan(luasLahan);
        String komoditasOk = wajib(komoditasUtama, "Komoditas Utama", 100);
        BigDecimal pendapatan = nominal(pendapatanTahunan, "Pendapatan Tahunan");

        pastikanBerhasil(eksekusi(() -> dao.tambahNasabahLengkap(nikOk, namaOk, alamatOk, telpOk,
                PETANI, luasOk, komoditasOk, pendapatan)), "Nasabah petani gagal ditambahkan.");
    }

    // Menambah nasabah UMKM sekaligus detail usaha (satu transaksi database).
    public void tambahUmkm(String nik, String nama, String alamat, String telepon,
                           String jenisUsaha, String lokasiUsaha, String omzetBulanan)
            throws ControllerException {
        wajibPeran(ADMIN, TELLER);
        String nikOk = validasiNik(nik);
        String namaOk = wajib(nama, "Nama Nasabah", 100);
        String alamatOk = wajib(alamat, "Alamat", 200);
        String telpOk = validasiTelepon(telepon);
        String usahaOk = wajib(jenisUsaha, "Jenis Usaha", 100);
        String lokasiOk = wajib(lokasiUsaha, "Lokasi Usaha", 200);
        BigDecimal omzet = nominal(omzetBulanan, "Omzet Bulanan");

        pastikanBerhasil(eksekusi(() -> dao.tambahNasabahLengkap(nikOk, namaOk, alamatOk, telpOk,
                UMKM, usahaOk, lokasiOk, omzet)), "Nasabah UMKM gagal ditambahkan.");
    }

    // ------------------------ ubah dan hapus ------------------------

    public void ubah(String nik, String nama, String alamat, String telepon, String jenis)
            throws ControllerException {
        wajibPeran(ADMIN, TELLER);
        String nikOk = validasiNik(nik);
        String namaOk = wajib(nama, "Nama Nasabah", 100);
        String alamatOk = wajib(alamat, "Alamat", 200);
        String telpOk = validasiTelepon(telepon);
        String jenisOk = pilihDari(jenis, "Jenis Nasabah", PETANI, UMKM);

        pastikanBerhasil(eksekusi(() -> dao.updateNasabah(nikOk, namaOk, alamatOk, telpOk, jenisOk)),
                "Data nasabah tidak ditemukan atau tidak berubah.");
    }

    public void hapus(String nik) throws ControllerException {
        wajibPeran(ADMIN, TELLER);
        String nikOk = validasiNik(nik);
        pastikanBerhasil(eksekusi(() -> dao.hapusNasabah(nikOk)), "Data nasabah tidak ditemukan.");
    }

    // ------------------------- validasi -----------------------------

    private String validasiNik(String nik) throws ControllerException {
        String hasil = wajib(nik, "NIK");
        if (!hasil.matches("\\d{16}")) {
            throw new ControllerException("NIK harus terdiri dari 16 digit angka!");
        }
        return hasil;
    }

    private String validasiTelepon(String telepon) throws ControllerException {
        String hasil = wajib(telepon, "No. Telepon");
        if (!hasil.matches("\\+?\\d{8,15}")) {
            throw new ControllerException("No. Telepon tidak valid (8-15 digit, boleh diawali +).");
        }
        return hasil;
    }

    private String validasiLuasLahan(String luas) throws ControllerException {
        String hasil = wajib(luas, "Luas Lahan").replace(',', '.');
        try {
            if (new BigDecimal(hasil).compareTo(BigDecimal.ZERO) <= 0) {
                throw new ControllerException("Luas Lahan harus lebih dari 0!");
            }
        } catch (NumberFormatException e) {
            throw new ControllerException("Luas Lahan harus berupa angka (contoh: 2.5).");
        }
        return hasil;
    }
}