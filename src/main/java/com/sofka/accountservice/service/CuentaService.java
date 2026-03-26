package com.sofka.accountservice.service;

import com.sofka.accountservice.domain.Cuenta;
import com.sofka.accountservice.domain.ClienteReferencia;
import com.sofka.accountservice.domain.Movimiento;
import com.sofka.accountservice.dto.CuentaRequest;
import com.sofka.accountservice.exception.CuentaNotFoundException;
import com.sofka.accountservice.exception.DuplicateCuentaException;
import com.sofka.accountservice.exception.ReporteInvalidoException;
import com.sofka.accountservice.exception.SaldoNoDisponibleException;
import com.sofka.accountservice.repository.ClienteReferenciaRepository;
import com.sofka.accountservice.repository.CuentaRepository;
import com.sofka.accountservice.repository.MovimientoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CuentaService implements CuentaApplicationService {

    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;
    private final ClienteReferenciaRepository clienteReferenciaRepository;

    public CuentaService(
            CuentaRepository cuentaRepository,
            MovimientoRepository movimientoRepository,
            ClienteReferenciaRepository clienteReferenciaRepository
    ) {
        this.cuentaRepository = cuentaRepository;
        this.movimientoRepository = movimientoRepository;
        this.clienteReferenciaRepository = clienteReferenciaRepository;
    }

    @Override
    @Transactional
    public Cuenta crear(Cuenta cuenta) {
        if (cuentaRepository.existsById(cuenta.getNumeroCuenta())) {
            throw new DuplicateCuentaException(cuenta.getNumeroCuenta());
        }
        sincronizarCliente(cuenta);
        cuenta.setSaldoDisponible(cuenta.getSaldoInicial());
        return cuentaRepository.save(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Cuenta> listar(Pageable pageable) {
        return cuentaRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Cuenta obtenerPorNumeroCuenta(Long numeroCuenta) {
        return cuentaRepository.findById(numeroCuenta)
                .orElseThrow(() -> new CuentaNotFoundException(numeroCuenta));
    }

    @Override
    @Transactional
    public Cuenta actualizar(Long numeroCuenta, CuentaRequest request) {
        if (!numeroCuenta.equals(request.numeroCuenta())) {
            throw new DuplicateCuentaException(request.numeroCuenta());
        }

        Cuenta cuenta = obtenerPorNumeroCuenta(numeroCuenta);
        cuenta.setTipoCuenta(request.tipoCuenta());
        cuenta.setSaldoInicial(request.saldoInicial());
        cuenta.setEstado(request.estado());
        cuenta.setClienteId(request.clienteId());
        sincronizarCliente(cuenta);

        recalcularSaldos(numeroCuenta);
        return cuentaRepository.save(cuenta);
    }

    @Override
    @Transactional
    public void eliminar(Long numeroCuenta) {
        cuentaRepository.delete(obtenerPorNumeroCuenta(numeroCuenta));
    }

    @Override
    @Transactional
    public void recalcularSaldos(Long numeroCuenta) {
        Cuenta cuenta = obtenerPorNumeroCuenta(numeroCuenta);
        List<Movimiento> movimientos = movimientoRepository.findByCuentaNumeroCuentaOrderByFechaAscMovimientoIdAsc(numeroCuenta);

        BigDecimal saldoActual = cuenta.getSaldoInicial();
        for (Movimiento movimiento : movimientos) {
            saldoActual = saldoActual.add(movimiento.getValor());
            if (saldoActual.compareTo(BigDecimal.ZERO) < 0) {
                throw new SaldoNoDisponibleException();
            }
            movimiento.setSaldo(saldoActual);
        }

        cuenta.setSaldoDisponible(saldoActual);
        movimientoRepository.saveAll(movimientos);
        cuentaRepository.save(cuenta);
    }

    private void sincronizarCliente(Cuenta cuenta) {
        ClienteReferencia cliente = clienteReferenciaRepository.findById(cuenta.getClienteId())
                .orElseThrow(() -> new ReporteInvalidoException(
                        "No existe referencia local para clienteId: " + cuenta.getClienteId()
                ));
        cuenta.setClienteNombre(cliente.getNombre());
    }
}