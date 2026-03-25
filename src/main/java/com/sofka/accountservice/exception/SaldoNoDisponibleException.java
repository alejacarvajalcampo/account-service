package com.sofka.accountservice.exception;

public class SaldoNoDisponibleException extends RuntimeException {

    public SaldoNoDisponibleException() {
        super("Saldo no disponible");
    }
}
