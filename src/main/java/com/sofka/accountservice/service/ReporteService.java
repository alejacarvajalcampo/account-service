package com.sofka.accountservice.service;

import com.sofka.accountservice.domain.Movimiento;
import com.sofka.accountservice.dto.ReporteEstadoCuentaResponse;
import com.sofka.accountservice.exception.ReporteInvalidoException;
import com.sofka.accountservice.repository.MovimientoRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReporteService implements ReporteApplicationService {

    private final MovimientoRepository movimientoRepository;

    public ReporteService(MovimientoRepository movimientoRepository) {
        this.movimientoRepository = movimientoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteEstadoCuentaResponse> generarReporte(LocalDate fechaDesde, LocalDate fechaHasta, Long clienteId) {
        if (clienteId == null) {
            throw new ReporteInvalidoException("clienteId es obligatorio para generar el reporte");
        }
        if (fechaDesde.isAfter(fechaHasta)) {
            throw new ReporteInvalidoException("fechaDesde no puede ser mayor que fechaHasta");
        }

        List<Movimiento> movimientos = movimientoRepository
                .findByCuentaClienteIdAndFechaBetweenOrderByFechaAscMovimientoIdAsc(clienteId, fechaDesde, fechaHasta);

        return movimientos.stream()
                .map(movimiento -> new ReporteEstadoCuentaResponse(
                        movimiento.getFecha(),
                        movimiento.getCuenta().getClienteNombre(),
                        movimiento.getCuenta().getNumeroCuenta(),
                        movimiento.getCuenta().getTipoCuenta(),
                        movimiento.getCuenta().getSaldoInicial(),
                        movimiento.getCuenta().getEstado(),
                        movimiento.getValor(),
                        movimiento.getSaldo()
                ))
                .toList();
    }
}
