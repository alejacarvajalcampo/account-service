package com.sofka.accountservice.service;

import com.sofka.accountservice.domain.Cuenta;
import com.sofka.accountservice.dto.CuentaRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CuentaApplicationService {

    Cuenta crear(Cuenta cuenta);

    Page<Cuenta> listar(Pageable pageable);

    Cuenta obtenerPorNumeroCuenta(Long numeroCuenta);

    Cuenta actualizar(Long numeroCuenta, CuentaRequest request);

    void eliminar(Long numeroCuenta);

    void recalcularSaldos(Long numeroCuenta);
}
