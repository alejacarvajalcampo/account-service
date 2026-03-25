package com.sofka.accountservice.exception;

public class CuentaNotFoundException extends RuntimeException {

    public CuentaNotFoundException(Long numeroCuenta) {
        super("Cuenta no encontrada con numeroCuenta: " + numeroCuenta);
    }
}
