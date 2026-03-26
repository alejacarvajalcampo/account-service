package com.sofka.accountservice.service;

import com.sofka.accountservice.dto.ReporteCuentaResponse;
import java.time.LocalDate;
import java.util.List;

public interface ReporteApplicationService {

    List<ReporteCuentaResponse> generarReporte(LocalDate fechaDesde, LocalDate fechaHasta, Long clienteId);
}
