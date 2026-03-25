package com.sofka.accountservice.exception;

public class MovimientoNotFoundException extends RuntimeException {

    public MovimientoNotFoundException(Long movimientoId) {
        super("Movimiento no encontrado con movimientoId: " + movimientoId);
    }
}
