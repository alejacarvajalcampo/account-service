package com.sofka.accountservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

public record ReporteCuentaResponse(
        @Schema(description = "Nombre del cliente", example = "Marianela Montalvo")
        String cliente,
        @Schema(description = "Numero de cuenta", example = "225487")
        Long numeroCuenta,
        @Schema(description = "Tipo de cuenta", example = "Corriente")
        String tipo,
        @Schema(description = "Saldo inicial de la cuenta", example = "100.00")
        BigDecimal saldoInicial,
        @Schema(description = "Estado de la cuenta", example = "true")
        Boolean estado,
        @Schema(description = "Movimientos agrupados de la cuenta")
        List<ReporteMovimientoResponse> movimientos
) {
}
