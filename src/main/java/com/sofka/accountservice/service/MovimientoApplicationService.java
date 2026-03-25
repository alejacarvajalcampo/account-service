package com.sofka.accountservice.service;

import com.sofka.accountservice.domain.Movimiento;
import com.sofka.accountservice.dto.MovimientoRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovimientoApplicationService {

    Movimiento crear(MovimientoRequest request);

    Page<Movimiento> listar(Pageable pageable);

    Movimiento obtenerPorId(Long movimientoId);

    Movimiento actualizar(Long movimientoId, MovimientoRequest request);

    void eliminar(Long movimientoId);
}
