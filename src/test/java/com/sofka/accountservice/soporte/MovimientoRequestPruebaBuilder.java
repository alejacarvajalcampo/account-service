package com.sofka.accountservice.soporte;

import com.sofka.accountservice.dto.MovimientoRequest;
import java.math.BigDecimal;
import java.time.LocalDate;

public class MovimientoRequestPruebaBuilder {

    private LocalDate fecha = LocalDate.of(2022, 2, 12);
    private String tipoMovimiento = "DEPOSITO";
    private BigDecimal valor = new BigDecimal("200.00");
    private Long numeroCuenta = 900001L;

    public static MovimientoRequestPruebaBuilder unMovimiento() {
        return new MovimientoRequestPruebaBuilder();
    }

    public MovimientoRequestPruebaBuilder conFecha(LocalDate fecha) {
        this.fecha = fecha;
        return this;
    }

    public MovimientoRequestPruebaBuilder conTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
        return this;
    }

    public MovimientoRequestPruebaBuilder conValor(BigDecimal valor) {
        this.valor = valor;
        return this;
    }

    public MovimientoRequestPruebaBuilder conNumeroCuenta(Long numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
        return this;
    }

    public MovimientoRequest construir() {
        return new MovimientoRequest(fecha, tipoMovimiento, valor, numeroCuenta);
    }
}