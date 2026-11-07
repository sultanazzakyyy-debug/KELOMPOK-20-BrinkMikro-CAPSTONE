/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.brinkmikro.app.controller;

import com.mycompany.brinkmikro.app.dao.AngsuranDAO;
import com.mycompany.brinkmikro.app.dao.AngsuranDAO.AngsuranRecord;
import com.mycompany.brinkmikro.app.dao.PembiayaanDAO;
import com.mycompany.brinkmikro.app.dao.PembiayaanDAO.PembiayaanRecord;
import com.mycompany.brinkmikro.app.model.Pengguna;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Adbang 18
 */
public class AngsuranController extends BaseController {

    private final AngsuranDAO dao;
    private final PembiayaanDAO pembiayaanDAO;

    public AngsuranController() {
        this(new AngsuranDAO(), new PembiayaanDAO());
    }

    public AngsuranController(AngsuranDAO dao, PembiayaanDAO pembiayaanDAO) {
        this.dao = dao;
        this.pembiayaanDAO = pembiayaanDAO;
    }

    @Override
    public String getNamaModul() {
        return "Angsuran";
    }

    public List<AngsuranRecord> daftar() throws ControllerException {
        penggunaAktif();
        return eksekusi(dao::getAllAngsuran);
    }

    public List<PembiayaanRecord> daftarPembiayaanBisaDibayar() throws ControllerException {
        penggunaAktif();
        List<PembiayaanRecord> hasil = new ArrayList<>();
        for (PembiayaanRecord p : eksekusi(pembiayaanDAO::getAllPembiayaan)) {
            if (PembiayaanController.DISETUJUI.equals(p.statusPembiayaan)) {
                hasil.add(p);
            }
        }
        return hasil;
    }

    public void bayar(String pilihanPembiayaan, String jumlahText, String dendaText)
            throws ControllerException {
        Pengguna user = wajibPeran(ADMIN, TELLER);
        int idPembiayaan = ambilId(pilihanPembiayaan, "Pembiayaan");
        BigDecimal jumlah = nominal(jumlahText, "Jumlah Bayar");
        BigDecimal denda = nominalBolehNol(dendaText, "Denda");

        // Teller mencatat atas namanya sendiri. Untuk Admin, DAO memilih teller yang valid.
        int tellerId = peranAdalah(user, TELLER)
                ? user.getIdPengguna()
                : eksekusi(() -> dao.getValidTellerId(user.getIdPengguna()));

        pastikanBerhasil(eksekusi(() -> dao.tambahAngsuran(idPembiayaan, jumlah, denda, tellerId)),
                "Pembayaran angsuran gagal disimpan.");
    }

    public void hapus(String idTampilan) throws ControllerException {
        wajibPeran(ADMIN);
        int id = ambilId(idTampilan, "ID Angsuran");
        pastikanBerhasil(eksekusi(() -> dao.hapusAngsuran(id)), "Data angsuran tidak ditemukan.");
    }
}
 