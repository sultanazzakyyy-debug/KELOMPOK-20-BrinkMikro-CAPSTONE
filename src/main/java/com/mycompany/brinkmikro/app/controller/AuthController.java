/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.brinkmikro.app.controller;

import com.mycompany.brinkmikro.app.config.UserSession;
import com.mycompany.brinkmikro.app.dao.PenggunaDAO;
import com.mycompany.brinkmikro.app.dao.PenggunaDAO.PenggunaRecord;
import com.mycompany.brinkmikro.app.model.Admin;
import com.mycompany.brinkmikro.app.model.Manajer;
import com.mycompany.brinkmikro.app.model.Pengguna;
import com.mycompany.brinkmikro.app.model.Teller;
import org.mindrot.jbcrypt.BCrypt;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author Adbang 18
 */
public class AuthController extends BaseController {

    public static final int MAKS_PERCOBAAN_GAGAL = 3;
    public static final int MIN_PANJANG_PASSWORD = 8;

    private final PenggunaDAO penggunaDAO;
    private final Map<String, Integer> percobaanGagal = new HashMap<>();

    public AuthController() {
        this(new PenggunaDAO());
    }

    public AuthController(PenggunaDAO penggunaDAO) {
        this.penggunaDAO = penggunaDAO;
    }

    @Override
    public String getNamaModul() {
        return "Autentikasi";
    }

    public Pengguna login(String username, String password, String peran) throws ControllerException {
        String user = wajib(username, "Username");
        if (password == null || password.isEmpty()) {
            throw new ControllerException("Kolom 'Password' tidak boleh kosong!");
        }
        // Panjang minimal password hanya berlaku saat membuat/mengubah password (PenggunaController),
        // BUKAN saat login. Akun lama/seed boleh punya password lebih pendek.
        String peranDipilih = pilihDari(peran, "Hak Akses", ADMIN, TELLER, MANAJER);

        PenggunaRecord akun = eksekusi(() -> penggunaDAO.cariUntukLogin(user, peranDipilih));
        if (akun == null) {
            throw new ControllerException("Username atau Hak Akses tidak sesuai!");
        }
        if (!"Aktif".equalsIgnoreCase(akun.statusAktif)) {
            throw new ControllerException("Akun berstatus: " + akun.statusAktif
                    + ". Silakan hubungi Admin.");
        }

        if (!passwordCocok(password, akun.password)) {
            catatGagal(user, akun.idPengguna);
        }

        percobaanGagal.remove(user);
        Pengguna masuk = buatObjekPeran(akun);
        UserSession.setLoginUser(masuk);
        return masuk;
    }

    // Mengakhiri sesi pengguna yang sedang login.
    public void logout() {
        UserSession.logout();
    }

    // Pengguna yang sedang login, atau null bila belum login.
    public Pengguna getPenggunaAktif() {
        return UserSession.getLoginUser();
    }

    private void catatGagal(String username, int idPengguna) throws ControllerException {
        int jumlah = percobaanGagal.getOrDefault(username, 0) + 1;
        if (jumlah >= MAKS_PERCOBAAN_GAGAL) {
            percobaanGagal.remove(username);
            eksekusi(() -> penggunaDAO.nonaktifkanAkun(idPengguna));
            throw new ControllerException("Gagal login " + MAKS_PERCOBAAN_GAGAL
                    + " kali. Akun DINONAKTIFKAN, silakan hubungi Admin.");
        }
        percobaanGagal.put(username, jumlah);
        throw new ControllerException("Password salah! Sisa percobaan: "
                + (MAKS_PERCOBAAN_GAGAL - jumlah));
    }

    // Hash bawaan (placeholder) pada data awal; password aslinya "123456".
    private static final String HASH_PLACEHOLDER = "$2y$10$wT8K8U9rEwSjK1M2N3O4PeX8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M";
    private static final String PASSWORD_PLACEHOLDER = "123456";

    private boolean passwordCocok(String input, String tersimpan) {
        if (tersimpan == null) {
            return false;
        }
        String hash = tersimpan.trim();
        if (HASH_PLACEHOLDER.equals(hash)) {
            return PASSWORD_PLACEHOLDER.equals(input);
        }
        if (hash.startsWith("$2a$") || hash.startsWith("$2y$") || hash.startsWith("$2b$")) {
            try {
                // jBCrypt 0.4 hanya mengenal prefix $2a$, jadi $2y$/$2b$ diseragamkan dulu.
                return BCrypt.checkpw(input, "$2a$" + hash.substring(4));
            } catch (IllegalArgumentException hashRusak) {
                return false;
            }
        }
        // Password lama yang masih tersimpan sebagai teks biasa.
        return input.equals(hash);
    }

    private Pengguna buatObjekPeran(PenggunaRecord a) {
        // Password sengaja tidak disimpan di sesi.
        if (ADMIN.equalsIgnoreCase(a.peran)) {
            return new Admin(a.idPengguna, a.namaLengkap, a.username, "", a.statusAktif, "", "");
        }
        if (TELLER.equalsIgnoreCase(a.peran)) {
            return new Teller(a.idPengguna, a.namaLengkap, a.username, "", a.statusAktif, "", "");
        }
        return new Manajer(a.idPengguna, a.namaLengkap, a.username, "", a.statusAktif, null, "");
    }
}
 