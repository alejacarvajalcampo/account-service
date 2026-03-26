package com.sofka.accountservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ReporteMovimientoResponse(
        @Schema(description = "Fecha del movimiento", example = "2022-02-10")
        LocalDate fecha,
        @Schema(description = "Valor del movimiento", example = "600.00")
        BigDecimal movimiento,
        @Schema(description = "Saldo disponible despues del movimiento", example = "700.00")
        BigDecimal saldoDisponible
) {
}
