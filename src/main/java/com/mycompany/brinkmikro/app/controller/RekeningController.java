/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.brinkmikro.app.controller;

import com.mycompany.brinkmikro.app.dao.NasabahDAO;
import com.mycompany.brinkmikro.app.dao.NasabahDAO.NasabahRecord;
import com.mycompany.brinkmikro.app.dao.RekeningTabunganDAO;
import com.mycompany.brinkmikro.app.dao.RekeningTabunganDAO.RekeningRecord;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author Adbang 18
 */
public class RekeningController extends BaseController {

    public static final String[] STATUS = {"Aktif", "Perlu Verifikasi", "Nonaktif", "Ditutup"};
    private static final String PREFIKS_BANK = "1001";

    private final RekeningTabunganDAO dao;
    private final NasabahDAO nasabahDAO;

    public RekeningController() {
        this(new RekeningTabunganDAO(), new NasabahDAO());
    }

    public RekeningController(RekeningTabunganDAO dao, NasabahDAO nasabahDAO) {
        this.dao = dao;
        this.nasabahDAO = nasabahDAO;
    }

    @Override
    public String getNamaModul() {
        return "Rekening Tabungan";
    }

    // Daftar rekening beserta nama nasabah pemiliknya. 
    public List<RekeningRecord> daftar() throws ControllerException {
        penggunaAktif();
        return eksekusi(dao::getAllRekening);
    }

    public String bukaRekening(String pilihanNasabah) throws ControllerException {
        wajibPeran(ADMIN, TELLER);
        String nik = ambilKunci(pilihanNasabah, "Nasabah");
        if (!petaNamaNasabah().containsKey(nik)) {
            throw new ControllerException("Nasabah dengan NIK " + nik + " tidak ditemukan.");
        }
        String nomor = buatNomorRekening();
        pastikanBerhasil(eksekusi(() -> dao.tambahRekening(nomor, nik)), "Rekening gagal dibuka.");
        return nomor;
    }

    // Membuka rekening lalu langsung menetapkan status awal yang dipilih. 
    public String bukaRekening(String pilihanNasabah, String statusAwal) throws ControllerException {
        String status = pilihDari(statusAwal, "Status Awal", STATUS);
        String nomor = bukaRekening(pilihanNasabah);
        if (!"Perlu Verifikasi".equals(status)) {
            pastikanBerhasil(eksekusi(() -> dao.updateStatusRekening(nomor, status)),
                    "Rekening dibuka, tetapi status awal gagal diatur.");
        }
        return nomor;
    }

    public void ubahStatus(String nomorRekening, String status) throws ControllerException {
        wajibPeran(ADMIN, TELLER);
        String nomor = wajib(nomorRekening, "Nomor Rekening", 30);
        String statusOk = pilihDari(status, "Status Rekening", STATUS);
        pastikanBerhasil(eksekusi(() -> dao.updateStatusRekening(nomor, statusOk)),
                "Rekening tidak ditemukan atau status tidak berubah.");
    }

    public void hapus(String nomorRekening) throws ControllerException {
        wajibPeran(ADMIN, TELLER);
        String nomor = wajib(nomorRekening, "Nomor Rekening", 30);
        pastikanBerhasil(eksekusi(() -> dao.hapusRekening(nomor)), "Rekening tidak ditemukan.");
    }

    // Format: 1001 + tahun + urutan 3 digit (mis. 10012026021), mengikuti data yang sudah ada. 
    private String buatNomorRekening() throws ControllerException {
        String prefiks = PREFIKS_BANK + Year.now().getValue();
        long urutanTerbesar = 0;
        for (RekeningRecord r : eksekusi(dao::getAllRekening)) {
            if (r.nomorRekening != null && r.nomorRekening.startsWith(prefiks)) {
                String sisa = r.nomorRekening.substring(prefiks.length());
                if (sisa.matches("\\d{1,9}")) {
                    urutanTerbesar = Math.max(urutanTerbesar, Long.parseLong(sisa));
                }
            }
        }
        return prefiks + String.format("%03d", urutanTerbesar + 1);
    }

    private Map<String, String> petaNamaNasabah() throws ControllerException {
        Map<String, String> peta = new HashMap<>();
        for (NasabahRecord n : eksekusi(nasabahDAO::getAllNasabah)) {
            peta.put(n.nik, n.namaNasabah);
        }
        return peta;
    }
}
 