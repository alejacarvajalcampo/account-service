package com.sofka.accountservice.support;

import com.sofka.accountservice.domain.Cuenta;
import java.math.BigDecimal;

public class CuentaTestDataBuilder {

    private Long numeroCuenta = 900001L;
    private String tipoCuenta = "Ahorro";
    private BigDecimal saldoInicial = new BigDecimal("100.00");
    private BigDecimal saldoDisponible = new BigDecimal("100.00");
    private Boolean estado = true;
    private Long clienteId = 99L;
    private String clienteNombre = "Cliente Prueba";

    public static CuentaTestDataBuilder unaCuenta() {
        return new CuentaTestDataBuilder();
    }

    public CuentaTestDataBuilder conNumeroCuenta(Long numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
        return this;
    }

    public CuentaTestDataBuilder conTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
        return this;
    }

    public CuentaTestDataBuilder conSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
        return this;
    }

    public CuentaTestDataBuilder conSaldoDisponible(BigDecimal saldoDisponible) {
        this.saldoDisponible = saldoDisponible;
        return this;
    }

    public CuentaTestDataBuilder conClienteId(Long clienteId) {
        this.clienteId = clienteId;
        return this;
    }

    public CuentaTestDataBuilder conClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
        return this;
    }

    public Cuenta build() {
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