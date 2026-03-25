package com.sofka.accountservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimientoRequest(
        @NotNull(message = "fecha es obligatoria")
        LocalDate fecha,
        @NotBlank(message = "tipoMovimiento es obligatorio")
        @Size(max = 30, message = "tipoMovimiento no puede superar 30 caracteres")
        String tipoMovimiento,
        @NotNull(message = "valor es obligatorio")
        BigDecimal valor,
        @NotNull(message = "numeroCuenta es obligatorio")
        Long numeroCuenta
) {
}
