/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.brinkmikro.app.controller;

import com.mycompany.brinkmikro.app.dao.TransaksiTabunganDAO;
import com.mycompany.brinkmikro.app.dao.TransaksiTabunganDAO.TransaksiRecord;
import com.mycompany.brinkmikro.app.model.Pengguna;
import java.math.BigDecimal;
import java.util.List;
/**
 *
 * @author Adbang 18
 */
public class TransaksiController extends BaseController {

    public static final String SETOR = "Setor Tunai";
    public static final String TARIK = "Tarik Tunai";

    private final TransaksiTabunganDAO dao;

    public TransaksiController() {
        this(new TransaksiTabunganDAO());
    }

    public TransaksiController(TransaksiTabunganDAO dao) {
        this.dao = dao;
    }

    @Override
    public String getNamaModul() {
        return "Transaksi Tabungan";
    }

    public List<TransaksiRecord> daftar() throws ControllerException {
        penggunaAktif();
        return eksekusi(dao::getAllTransaksi);
    }

    // Daftar teller aktif, tiap elemen {id, nama}, untuk dropdown petugas. 
    public List<String[]> daftarTeller() throws ControllerException {
        penggunaAktif();
        return eksekusi(dao::getDaftarTeller);
    }

    public BigDecimal proses(String nomorRekening, String jenis, String nominalText, String pilihanTeller)
            throws ControllerException {
        Pengguna user = wajibPeran(ADMIN, TELLER);
        String nomor = wajib(nomorRekening, "Nomor Rekening", 30);
        String jenisOk = pilihDari(jenis, "Jenis Transaksi", SETOR, TARIK);
        BigDecimal jumlah = nominal(nominalText, "Jumlah");
        int tellerId = peranAdalah(user, TELLER)
                ? user.getIdPengguna()
                : ambilId(pilihanTeller, "Teller Pemroses");

        return eksekusi(() -> dao.prosesTransaksi(nomor, jenisOk, jumlah, tellerId));
    }

    // Versi tanpa pilihan teller; hanya berlaku untuk pengguna yang login sebagai Teller.
    public BigDecimal proses(String nomorRekening, String jenis, String nominalText)
            throws ControllerException {
        return proses(nomorRekening, jenis, nominalText, null);
    }
}
 