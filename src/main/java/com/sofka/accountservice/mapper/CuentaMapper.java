package com.sofka.accountservice.mapper;

import com.sofka.accountservice.domain.Cuenta;
import com.sofka.accountservice.dto.CuentaRequest;
import com.sofka.accountservice.dto.CuentaResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CuentaMapper {

    @Mapping(target = "saldoDisponible", ignore = true)
    @Mapping(target = "movimientos", ignore = true)
    Cuenta toEntity(CuentaRequest request);

    CuentaResponse toResponse(Cuenta cuenta);

    @AfterMapping
    default void setSaldoDisponible(@MappingTarget Cuenta cuenta, CuentaRequest request) {
        cuenta.setSaldoDisponible(request.saldoInicial());
    }
}
