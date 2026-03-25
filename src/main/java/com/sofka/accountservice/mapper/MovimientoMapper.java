package com.sofka.accountservice.mapper;

import com.sofka.accountservice.domain.Movimiento;
import com.sofka.accountservice.dto.MovimientoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovimientoMapper {

    @Mapping(target = "numeroCuenta", source = "cuenta.numeroCuenta")
    MovimientoResponse toResponse(Movimiento movimiento);
}
