package com.sofka.accountservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimientoResponse(
        Long movimientoId,
        LocalDate fecha,
        String tipoMovimiento,
        BigDecimal valor,
        BigDecimal saldo,
        Long numeroCuenta
) {
}
