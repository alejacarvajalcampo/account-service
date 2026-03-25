package com.sofka.accountservice.controller;

import com.sofka.accountservice.dto.ReporteEstadoCuentaResponse;
import com.sofka.accountservice.service.ReporteApplicationService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reportes")
public class ReporteController {

    private final ReporteApplicationService reporteService;

    public ReporteController(ReporteApplicationService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    public ResponseEntity<List<ReporteEstadoCuentaResponse>> generar(
            @RequestParam("fechaDesde") LocalDate fechaDesde,
            @RequestParam("fechaHasta") LocalDate fechaHasta,
            @RequestParam("clienteId") Long clienteId
    ) {
        return ResponseEntity.ok(reporteService.generarReporte(fechaDesde, fechaHasta, clienteId));
    }
}
