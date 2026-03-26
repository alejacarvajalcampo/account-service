package com.sofka.accountservice.support;

import com.sofka.accountservice.dto.MovimientoRequest;
import java.math.BigDecimal;
import java.time.LocalDate;

public class MovimientoRequestTestDataBuilder {

    private LocalDate fecha = LocalDate.of(2022, 2, 12);
    private String tipoMovimiento = "DEPOSITO";
    private BigDecimal valor = new BigDecimal("200.00");
    private Long numeroCuenta = 900001L;

    public static MovimientoRequestTestDataBuilder unMovimiento() {
        return new MovimientoRequestTestDataBuilder();
    }

    public MovimientoRequestTestDataBuilder conFecha(LocalDate fecha) {
        this.fecha = fecha;
        return this;
    }

    public MovimientoRequestTestDataBuilder conTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
        return this;
    }

    public MovimientoRequestTestDataBuilder conValor(BigDecimal valor) {
        this.valor = valor;
        return this;
    }

    public MovimientoRequestTestDataBuilder conNumeroCuenta(Long numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
        return this;
    }

    public MovimientoRequest build() {
        return new MovimientoRequest(fecha, tipoMovimiento, valor, numeroCuenta);
    }
}