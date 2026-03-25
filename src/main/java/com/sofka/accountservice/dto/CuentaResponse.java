package com.sofka.accountservice.dto;

import java.math.BigDecimal;

public record CuentaResponse(
        Long numeroCuenta,
        String tipoCuenta,
        BigDecimal saldoInicial,
        BigDecimal saldoDisponible,
        Boolean estado,
        Long clienteId,
        String clienteNombre
) {
}
