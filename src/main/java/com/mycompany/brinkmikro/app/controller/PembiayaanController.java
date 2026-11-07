/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.brinkmikro.app.controller;

import com.mycompany.brinkmikro.app.dao.PembiayaanDAO;
import com.mycompany.brinkmikro.app.dao.PembiayaanDAO.PembiayaanRecord;
import com.mycompany.brinkmikro.app.model.Pengguna;
import java.math.BigDecimal;
import java.util.List;
/**
 *
 * @author Adbang 18
 */
public class PembiayaanController extends BaseController {

    public static final BigDecimal BUNGA_DEFAULT = new BigDecimal("6.00");
    public static final String[] SKEMA = {"Mingguan", "Bulanan", "Musiman Panen"};
    public static final int JANGKA_MIN_BULAN = 1;
    public static final int JANGKA_MAKS_BULAN = 120;

    public static final String DIAJUKAN = "Diajukan";
    public static final String DISETUJUI = "Disetujui";
    public static final String DITOLAK = "Ditolak";
    public static final String LUNAS = "Lunas";

    private final PembiayaanDAO dao;

    public PembiayaanController() {
        this(new PembiayaanDAO());
    }

    public PembiayaanController(PembiayaanDAO dao) {
        this.dao = dao;
    }

    @Override
    public String getNamaModul() {
        return "Pengajuan Pembiayaan";
    }

    // ---------------------------- baca ----------------------------

    public List<PembiayaanRecord> daftar() throws ControllerException {
        penggunaAktif();
        return eksekusi(dao::getAllPembiayaan);
    }

    public PembiayaanRecord cari(String idTampilan) throws ControllerException {
        int id = ambilId(idTampilan, "ID Pembiayaan");
        for (PembiayaanRecord p : daftar()) {
            if (p.idPembiayaan == id) {
                return p;
            }
        }
        throw new ControllerException("Data pembiayaan PMB-" + id + " tidak ditemukan.");
    }

    // Pratinjau total kewajiban (pokok + bunga proporsional) sebelum pengajuan disimpan. 
    public BigDecimal hitungTotalKewajiban(String jumlahText, String jangkaText) throws ControllerException {
        BigDecimal jumlah = nominal(jumlahText, "Jumlah Pengajuan");
        int bulan = validasiJangka(jangkaText);
        return dao.hitungTotalKewajiban(jumlah, bulan, BUNGA_DEFAULT);
    }

    // ------------------------ pengajuan baru ------------------------

    public BigDecimal ajukan(String pilihanNasabah, String jumlahText, String jangkaText, String skema)
            throws ControllerException {
        wajibPeran(ADMIN, TELLER);
        String nik = ambilKunci(pilihanNasabah, "Nasabah");
        BigDecimal jumlah = nominal(jumlahText, "Jumlah Pengajuan");
        int bulan = validasiJangka(jangkaText);
        String skemaOk = pilihDari(skema, "Skema Pembiayaan", SKEMA);

        pastikanBerhasil(eksekusi(() ->
                dao.tambahPengajuan(jumlah, bulan, BUNGA_DEFAULT, skemaOk, null, nik)),
                "Pengajuan gagal disimpan.");
        return dao.hitungTotalKewajiban(jumlah, bulan, BUNGA_DEFAULT);
    }

    // Mengubah pengajuan yang masih berstatus Diajukan. {@code skorText} boleh kosong. 
    public void ubah(String idTampilan, String jumlahText, String jangkaText, String skema, String skorText)
            throws ControllerException {
        wajibPeran(ADMIN, TELLER);
        PembiayaanRecord lama = cari(idTampilan);
        if (!DIAJUKAN.equals(lama.statusPembiayaan)) {
            throw new ControllerException("Hanya pengajuan berstatus 'Diajukan' yang dapat diubah.");
        }
        BigDecimal jumlah = nominal(jumlahText, "Jumlah Pengajuan");
        int bulan = validasiJangka(jangkaText);
        String skemaOk = pilihDari(skema, "Skema Pembiayaan", SKEMA);
        Integer skor = (skorText == null || skorText.trim().isEmpty()) ? null : validasiSkor(skorText);
        BigDecimal bunga = lama.sukuBungaPersen != null ? lama.sukuBungaPersen : BUNGA_DEFAULT;

        pastikanBerhasil(eksekusi(() ->
                dao.updatePembiayaan(lama.idPembiayaan, jumlah, bulan, bunga, skemaOk, skor)),
                "Pengajuan tidak ditemukan atau tidak berubah.");
    }

    // ------------------- verifikasi dan keputusan -------------------

    public void verifikasi(String idTampilan, String status, String skorText) throws ControllerException {
        Pengguna user = wajibPeran(ADMIN, MANAJER);
        PembiayaanRecord p = cari(idTampilan);
        String statusOk = pilihDari(status, "Status", DIAJUKAN, DISETUJUI, DITOLAK);
        int skor = validasiSkor(skorText);

        if (!DIAJUKAN.equals(p.statusPembiayaan)) {
            throw new ControllerException("Pembiayaan berstatus '" + p.statusPembiayaan
                    + "' sudah diputuskan dan tidak dapat diverifikasi ulang.");
        }

        int idManajer = -1; // -1 = tidak ada manajer (disimpan sebagai NULL)
        if (!DIAJUKAN.equals(statusOk)) {
            wajibPeran(MANAJER);
            idManajer = user.getIdPengguna();
        }
        final int manajerId = idManajer;

        if (DISETUJUI.equals(statusOk)) {
            // putuskanPembiayaan memeriksa batas persetujuan manajer
            pastikanBerhasil(eksekusi(() -> dao.putuskanPembiayaan(p.idPembiayaan, manajerId, true)),
                    "Keputusan gagal disimpan.");
        }
        pastikanBerhasil(eksekusi(() ->
                dao.verifikasiPengajuan(p.idPembiayaan, statusOk, skor, manajerId)),
                "Data verifikasi gagal disimpan.");
    }

    // Keputusan Manajer atas pengajuan berstatus Diajukan.
    public void putuskan(String idTampilan, boolean disetujui) throws ControllerException {
        Pengguna manajer = wajibPeran(MANAJER);
        PembiayaanRecord p = cari(idTampilan);
        if (!DIAJUKAN.equals(p.statusPembiayaan)) {
            throw new ControllerException("Pembiayaan berstatus '" + p.statusPembiayaan
                    + "' sudah diputuskan.");
        }
        pastikanBerhasil(eksekusi(() ->
                dao.putuskanPembiayaan(p.idPembiayaan, manajer.getIdPengguna(), disetujui)),
                "Keputusan gagal disimpan.");
    }

    // Menghapus/membatalkan pengajuan yang belum disetujui (Diajukan atau Ditolak).
    public void hapus(String idTampilan) throws ControllerException {
        wajibPeran(ADMIN, TELLER);
        PembiayaanRecord p = cari(idTampilan);
        if (DISETUJUI.equals(p.statusPembiayaan) || LUNAS.equals(p.statusPembiayaan)) {
            throw new ControllerException("Pembiayaan berstatus '" + p.statusPembiayaan
                    + "' tidak dapat dihapus karena sudah menjadi catatan keuangan.");
        }
        pastikanBerhasil(eksekusi(() -> dao.hapusPengajuan(p.idPembiayaan)), "Pengajuan tidak ditemukan.");
    }

    // ------------------------- validasi -----------------------------

    private int validasiJangka(String jangkaText) throws ControllerException {
        int bulan = angkaBulat(jangkaText, "Jangka Waktu (Bulan)");
        if (bulan < JANGKA_MIN_BULAN || bulan > JANGKA_MAKS_BULAN) {
            throw new ControllerException("Jangka waktu harus antara " + JANGKA_MIN_BULAN
                    + " dan " + JANGKA_MAKS_BULAN + " bulan.");
        }
        return bulan;
    }

    private int validasiSkor(String skorText) throws ControllerException {
        int skor = angkaBulat(skorText, "Skor Kelayakan");
        if (skor > 100) {
            throw new ControllerException("Skor kelayakan harus antara 0 dan 100.");
        }
        return skor;
    }
}