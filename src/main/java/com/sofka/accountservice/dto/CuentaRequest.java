package com.sofka.accountservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CuentaRequest(
        @Schema(description = "Numero unico de cuenta", example = "585545")
        @NotNull(message = "numeroCuenta es obligatorio")
        Long numeroCuenta,
        @Schema(description = "Tipo de cuenta", example = "Corriente")
        @NotBlank(message = "tipoCuenta es obligatorio")
        @Size(max = 30, message = "tipoCuenta no puede superar 30 caracteres")
        String tipoCuenta,
        @Schema(description = "Saldo inicial de la cuenta", example = "1000.00")
        @NotNull(message = "saldoInicial es obligatorio")
        @DecimalMin(value = "0.00", message = "saldoInicial debe ser mayor o igual a 0")
        BigDecimal saldoInicial,
        @Schema(description = "Estado de la cuenta", example = "true")
        @NotNull(message = "estado es obligatorio")
        Boolean estado,
        @Schema(description = "Identificador del cliente asociado", example = "2")
        @NotNull(message = "clienteId es obligatorio")
        Long clienteId,
        @Schema(description = "Nombre del cliente asociado. Se sincroniza con el evento del microservicio de clientes", example = "Marianela Montalvo")
        @NotBlank(message = "clienteNombre es obligatorio")
        @Size(max = 100, message = "clienteNombre no puede superar 100 caracteres")
        String clienteNombre
) {
}
