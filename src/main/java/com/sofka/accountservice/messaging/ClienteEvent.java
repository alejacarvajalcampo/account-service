package com.sofka.accountservice.messaging;

public record ClienteEvent(
        TipoEventoCliente eventType,
        Long clienteId,
        String nombre,
        String identificacion,
        Boolean estado
) {
}