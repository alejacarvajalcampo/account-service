package com.sofka.accountservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReporteEstadoCuentaResponse(
        LocalDate fecha,
        String cliente,
        Long numeroCuenta,
        String tipo,
        BigDecimal saldoInicial,
        Boolean estado,
        BigDecimal movimiento,
        BigDecimal saldoDisponible
) {
}
