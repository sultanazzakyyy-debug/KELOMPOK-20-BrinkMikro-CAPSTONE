/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.brinkmikro.app.controller;

import com.mycompany.brinkmikro.app.dao.PenggunaDAO;
import com.mycompany.brinkmikro.app.dao.PenggunaDAO.PenggunaRecord;
import com.mycompany.brinkmikro.app.model.Pengguna;
import java.util.List;
/**
 *
 * @author Adbang 18
 */
public class PenggunaController extends BaseController {

    private static final String[] PERAN = {ADMIN, TELLER, MANAJER};
    private static final String[] STATUS = {"Aktif", "Terkunci", "Nonaktif"};
    private static final int MAKS_PASSWORD = 72; // batas BCrypt

    private final PenggunaDAO dao;

    public PenggunaController() {
        this(new PenggunaDAO());
    }

    public PenggunaController(PenggunaDAO dao) {
        this.dao = dao;
    }

    @Override
    public String getNamaModul() {
        return "Data Pengguna";
    }

    public List<PenggunaRecord> daftar() throws ControllerException {
        wajibPeran(ADMIN);
        return eksekusi(dao::getAllPengguna);
    }

    public void tambah(String nama, String username, String password, String peran)
            throws ControllerException {
        wajibPeran(ADMIN);
        String namaOk = wajib(nama, "Nama Lengkap", 100);
        String userOk = validasiUsername(username);
        String passOk = validasiPassword(password);
        String peranOk = pilihDari(peran, "Role / Akses", PERAN);

        pastikanBerhasil(
                eksekusi(() -> dao.tambahPengguna(namaOk, userOk, passOk, peranOk, null, null, null)),
                "Pengguna gagal ditambahkan.");
    }

    public void ubah(String idTampilan, String nama, String username, String passwordBaru,
                     String peran, String status) throws ControllerException {
        Pengguna admin = wajibPeran(ADMIN);
        int id = ambilId(idTampilan, "ID Pengguna");
        String namaOk = wajib(nama, "Nama Lengkap", 100);
        String userOk = validasiUsername(username);
        String peranOk = pilihDari(peran, "Role / Akses", PERAN);
        String statusOk = pilihDari(status, "Status Akun", STATUS);

        if (id == admin.getIdPengguna() && (!ADMIN.equals(peranOk) || !"Aktif".equals(statusOk))) {
            throw new ControllerException("Anda tidak dapat menonaktifkan atau mengubah peran "
                    + "akun yang sedang digunakan.");
        }

        boolean berhasil;
        if (passwordBaru == null || passwordBaru.trim().isEmpty()) {
            berhasil = eksekusi(() -> dao.updatePenggunaTanpaPassword(id, namaOk, userOk, peranOk, statusOk));
        } else {
            String passOk = validasiPassword(passwordBaru);
            berhasil = eksekusi(() -> dao.updatePenggunaLengkap(id, namaOk, userOk, passOk, peranOk, statusOk));
        }
        pastikanBerhasil(berhasil, "Data pengguna tidak ditemukan atau tidak berubah.");
    }

    public void hapus(String idTampilan) throws ControllerException {
        Pengguna admin = wajibPeran(ADMIN);
        int id = ambilId(idTampilan, "ID Pengguna");
        if (id == admin.getIdPengguna()) {
            throw new ControllerException("Anda tidak dapat menghapus akun yang sedang digunakan.");
        }
        pastikanBerhasil(eksekusi(() -> dao.hapusPengguna(id)), "Data pengguna tidak ditemukan.");
    }

    private String validasiUsername(String username) throws ControllerException {
        String hasil = wajib(username, "Username", 50);
        if (!hasil.matches("[A-Za-z0-9._]{4,50}")) {
            throw new ControllerException("Username 4-50 karakter dan hanya boleh huruf, angka, "
                    + "titik, atau garis bawah (tanpa spasi).");
        }
        return hasil;
    }

    private String validasiPassword(String password) throws ControllerException {
        if (password == null || password.trim().length() < AuthController.MIN_PANJANG_PASSWORD) {
            throw new ControllerException("Password minimal "
                    + AuthController.MIN_PANJANG_PASSWORD + " karakter.");
        }
        if (password.length() > MAKS_PASSWORD) {
            throw new ControllerException("Password maksimal " + MAKS_PASSWORD + " karakter.");
        }
        return password;
    }
}
 