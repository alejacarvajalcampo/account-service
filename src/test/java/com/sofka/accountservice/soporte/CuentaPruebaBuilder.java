package com.sofka.accountservice.soporte;

import com.sofka.accountservice.domain.Cuenta;
import java.math.BigDecimal;

public class CuentaPruebaBuilder {

    private Long numeroCuenta = 900001L;
    private String tipoCuenta = "Ahorro";
    private BigDecimal saldoInicial = new BigDecimal("100.00");
    private BigDecimal saldoDisponible = new BigDecimal("100.00");
    private Boolean estado = true;
    private Long clienteId = 99L;
    private String clienteNombre = "Cliente Prueba";

    public static CuentaPruebaBuilder unaCuenta() {
        return new CuentaPruebaBuilder();
    }

    public CuentaPruebaBuilder conNumeroCuenta(Long numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
        return this;
    }

    public CuentaPruebaBuilder conTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
        return this;
    }

    public CuentaPruebaBuilder conSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
        return this;
    }

    public CuentaPruebaBuilder conSaldoDisponible(BigDecimal saldoDisponible) {
        this.saldoDisponible = saldoDisponible;
        return this;
    }

    public CuentaPruebaBuilder conClienteId(Long clienteId) {
        this.clienteId = clienteId;
        return this;
    }

    public CuentaPruebaBuilder conClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
        return this;
    }

    public Cuenta construir() {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(numeroCuenta);
        cuenta.setTipoCuenta(tipoCuenta);
        cuenta.setSaldoInicial(saldoInicial);
        cuenta.setSaldoDisponible(saldoDisponible);
        cuenta.setEstado(estado);
        cuenta.setClienteId(clienteId);
        cuenta.setClienteNombre(clienteNombre);
        return cuenta;
    }
}