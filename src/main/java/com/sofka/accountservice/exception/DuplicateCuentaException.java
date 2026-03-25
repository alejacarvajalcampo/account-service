package com.sofka.accountservice.exception;

public class DuplicateCuentaException extends RuntimeException {

    public DuplicateCuentaException(Long numeroCuenta) {
        super("Ya existe una cuenta con numeroCuenta: " + numeroCuenta);
    }
}
