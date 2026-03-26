package com.sofka.accountservice.service;

import com.sofka.accountservice.domain.Cuenta;
import com.sofka.accountservice.domain.Movimiento;
import com.sofka.accountservice.dto.MovimientoRequest;
import com.sofka.accountservice.exception.MovimientoNotFoundException;
import com.sofka.accountservice.repository.MovimientoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovimientoService implements MovimientoApplicationService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaApplicationService cuentaService;

    public MovimientoService(MovimientoRepository movimientoRepository, CuentaApplicationService cuentaService) {
        this.movimientoRepository = movimientoRepository;
        this.cuentaService = cuentaService;
    }

    @Override
    @Transactional
    public Movimiento crear(MovimientoRequest request) {
        Cuenta cuenta = cuentaService.obtenerPorNumeroCuenta(request.numeroCuenta());

        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(request.fecha());
        movimiento.setTipoMovimiento(request.tipoMovimiento());
        movimiento.setValor(request.valor());
        movimiento.setCuenta(cuenta);
        movimiento.setSaldo(cuenta.getSaldoDisponible());

        Movimiento saved = movimientoRepository.save(movimiento);
        cuentaService.recalcularSaldos(cuenta.getNumeroCuenta());
        return movimientoRepository.findById(saved.getMovimientoId())
                .orElseThrow(() -> new MovimientoNotFoundException(saved.getMovimientoId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Movimiento> listar(Pageable pageable) {
        return movimientoRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Movimiento obtenerPorId(Long movimientoId) {
        return movimientoRepository.findById(movimientoId)
                .orElseThrow(() -> new MovimientoNotFoundException(movimientoId));
    }

    @Override
    @Transactional
    public Movimiento actualizar(Long movimientoId, MovimientoRequest request) {
        Movimiento movimiento = obtenerPorId(movimientoId);
        Long cuentaAnterior = movimiento.getCuenta().getNumeroCuenta();
        Cuenta cuentaNueva = cuentaService.obtenerPorNumeroCuenta(request.numeroCuenta());

        movimiento.setFecha(request.fecha());
        movimiento.setTipoMovimiento(request.tipoMovimiento());
        movimiento.setValor(request.valor());
        movimiento.setCuenta(cuentaNueva);

        movimientoRepository.save(movimiento);
        cuentaService.recalcularSaldos(cuentaNueva.getNumeroCuenta());
        if (!cuentaAnterior.equals(cuentaNueva.getNumeroCuenta())) {
            cuentaService.recalcularSaldos(cuentaAnterior);
        }

        return obtenerPorId(movimientoId);
    }

    @Override
    @Transactional
    public void eliminar(Long movimientoId) {
        Movimiento movimiento = obtenerPorId(movimientoId);
        Long numeroCuenta = movimiento.getCuenta().getNumeroCuenta();
        movimientoRepository.delete(movimiento);
        cuentaService.recalcularSaldos(numeroCuenta);
    }
}