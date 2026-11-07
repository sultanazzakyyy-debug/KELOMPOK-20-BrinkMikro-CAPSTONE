/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.brinkmikro.app.controller;

import com.mycompany.brinkmikro.app.dao.DashboardKasDAO;
import com.mycompany.brinkmikro.app.dao.DashboardKasDAO.ArusKasHarian;
import com.mycompany.brinkmikro.app.dao.DashboardKasDAO.SummaryKas;
import com.mycompany.brinkmikro.app.dao.MonitoringDAO;
import com.mycompany.brinkmikro.app.dao.MonitoringDAO.JatuhTempoRecord;
import com.mycompany.brinkmikro.app.dao.MonitoringDAO.RekapRecord;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
/**
 *
 * @author Adbang 18
 */
public class MonitoringController extends BaseController {

    private final DashboardKasDAO kasDAO;
    private final MonitoringDAO monitoringDAO;

    public MonitoringController() {
        this(new DashboardKasDAO(), new MonitoringDAO());
    }

    public MonitoringController(DashboardKasDAO kasDAO, MonitoringDAO monitoringDAO) {
        this.kasDAO = kasDAO;
        this.monitoringDAO = monitoringDAO;
    }

    @Override
    public String getNamaModul() {
        return "Monitoring dan Laporan";
    }

    // Ringkasan kas: total saldo tabungan, kas masuk/keluar hari ini, dan posisi likuiditas.
    public SummaryKas ringkasanKas() throws ControllerException {
        wajibPeran(ADMIN, MANAJER);
        return eksekusi(kasDAO::getSummaryKas);
    }

    public List<ArusKasHarian> arusKasHarian() throws ControllerException {
        wajibPeran(ADMIN, MANAJER);
        return eksekusi(kasDAO::getRekapArusKasHarian);
    }

    public Map<String, BigDecimal> dashboardKas() throws ControllerException {
        wajibPeran(ADMIN, MANAJER);
        return eksekusi(monitoringDAO::getDashboardKas);
    }

    public List<JatuhTempoRecord> jatuhTempo() throws ControllerException {
        wajibPeran(ADMIN, MANAJER);
        return eksekusi(monitoringDAO::getDaftarJatuhTempo);
    }

    public List<RekapRecord> rekapPembiayaan() throws ControllerException {
        wajibPeran(ADMIN, MANAJER);
        return eksekusi(monitoringDAO::getRekapLaporanPembiayaan);
    }
}
 