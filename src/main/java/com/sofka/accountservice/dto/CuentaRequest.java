package com.sofka.accountservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CuentaRequest(
        @NotNull(message = "numeroCuenta es obligatorio")
        Long numeroCuenta,
        @NotBlank(message = "tipoCuenta es obligatorio")
        @Size(max = 30, message = "tipoCuenta no puede superar 30 caracteres")
        String tipoCuenta,
        @NotNull(message = "saldoInicial es obligatorio")
        @DecimalMin(value = "0.00", message = "saldoInicial debe ser mayor o igual a 0")
        BigDecimal saldoInicial,
        @NotNull(message = "estado es obligatorio")
        Boolean estado,
        @NotNull(message = "clienteId es obligatorio")
        Long clienteId,
        @NotBlank(message = "clienteNombre es obligatorio")
        @Size(max = 100, message = "clienteNombre no puede superar 100 caracteres")
        String clienteNombre
) {
}
