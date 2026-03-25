package com.sofka.accountservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimientoRequest(
        @Schema(description = "Fecha del movimiento", example = "2022-02-10")
        @NotNull(message = "fecha es obligatoria")
        LocalDate fecha,
        @Schema(description = "Tipo de movimiento", example = "DEPOSITO")
        @NotBlank(message = "tipoMovimiento es obligatorio")
        @Size(max = 30, message = "tipoMovimiento no puede superar 30 caracteres")
        String tipoMovimiento,
        @Schema(description = "Valor del movimiento. Depositos positivos, retiros negativos", example = "600.00")
        @NotNull(message = "valor es obligatorio")
        BigDecimal valor,
        @Schema(description = "Numero de cuenta afectada", example = "225487")
        @NotNull(message = "numeroCuenta es obligatorio")
        Long numeroCuenta
) {
}
