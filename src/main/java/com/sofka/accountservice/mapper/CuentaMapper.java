package com.sofka.accountservice.mapper;

import com.sofka.accountservice.domain.Cuenta;
import com.sofka.accountservice.dto.CuentaRequest;
import com.sofka.accountservice.dto.CuentaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CuentaMapper {

    @Mapping(target = "saldoDisponible", ignore = true)
    @Mapping(target = "movimientos", ignore = true)
    @Mapping(target = "clienteNombre", ignore = true)
    Cuenta toEntity(CuentaRequest request);

    CuentaResponse toResponse(Cuenta cuenta);
}
