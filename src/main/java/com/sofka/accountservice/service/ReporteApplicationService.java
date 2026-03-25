package com.sofka.accountservice.service;

import com.sofka.accountservice.dto.ReporteEstadoCuentaResponse;
import java.time.LocalDate;
import java.util.List;

public interface ReporteApplicationService {

    List<ReporteEstadoCuentaResponse> generarReporte(LocalDate fechaDesde, LocalDate fechaHasta, Long clienteId);
}
