package com.sofka.accountservice.soporte;

import com.sofka.accountservice.domain.ClienteReferencia;

public class ClienteReferenciaPruebaBuilder {

    private Long clienteId = 1L;
    private String nombre = "Jose Lema";
    private String identificacion = "1234567890";
    private Boolean estado = true;

    public static ClienteReferenciaPruebaBuilder unClienteReferencia() {
        return new ClienteReferenciaPruebaBuilder();
    }

    public ClienteReferenciaPruebaBuilder conClienteId(Long clienteId) {
        this.clienteId = clienteId;
        return this;
    }

    public ClienteReferenciaPruebaBuilder conNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public ClienteReferenciaPruebaBuilder conIdentificacion(String identificacion) {
        this.identificacion = identificacion;
        return this;
    }

    public ClienteReferencia construir() {
        ClienteReferencia cliente = new ClienteReferencia();
        cliente.setClienteId(clienteId);
        cliente.setNombre(nombre);
        cliente.setIdentificacion(identificacion);
        cliente.setEstado(estado);
        return cliente;
    }
}