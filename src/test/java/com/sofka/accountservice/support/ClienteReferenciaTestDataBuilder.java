package com.sofka.accountservice.support;

import com.sofka.accountservice.domain.ClienteReferencia;

public class ClienteReferenciaTestDataBuilder {

    private Long clienteId = 1L;
    private String nombre = "Jose Lema";
    private String identificacion = "1234567890";
    private Boolean estado = true;

    public static ClienteReferenciaTestDataBuilder unClienteReferencia() {
        return new ClienteReferenciaTestDataBuilder();
    }

    public ClienteReferenciaTestDataBuilder conClienteId(Long clienteId) {
        this.clienteId = clienteId;
        return this;
    }

    public ClienteReferenciaTestDataBuilder conNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public ClienteReferenciaTestDataBuilder conIdentificacion(String identificacion) {
        this.identificacion = identificacion;
        return this;
    }

    public ClienteReferencia build() {
        ClienteReferencia cliente = new ClienteReferencia();
        cliente.setClienteId(clienteId);
        cliente.setNombre(nombre);
        cliente.setIdentificacion(identificacion);
        cliente.setEstado(estado);
        return cliente;
    }
}