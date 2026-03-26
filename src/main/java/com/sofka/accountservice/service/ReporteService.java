package com.sofka.accountservice.service;

import com.sofka.accountservice.domain.Movimiento;
import com.sofka.accountservice.dto.ReporteCuentaResponse;
import com.sofka.accountservice.dto.ReporteMovimientoResponse;
import com.sofka.accountservice.exception.ReporteInvalidoException;
import com.sofka.accountservice.repository.MovimientoRepository;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    public List<ReporteCuentaResponse> generarReporte(LocalDate fechaDesde, LocalDate fechaHasta, Long clienteId) {
        if (clienteId == null) {
            throw new ReporteInvalidoException("clienteId es obligatorio para generar el reporte");
        }
        if (fechaDesde.isAfter(fechaHasta)) {
            throw new ReporteInvalidoException("fechaDesde no puede ser mayor que fechaHasta");
        }

        List<Movimiento> movimientos = movimientoRepository
                .findByCuentaClienteIdAndFechaBetweenOrderByFechaAscMovimientoIdAsc(clienteId, fechaDesde, fechaHasta);

        Map<Long, List<Movimiento>> agrupados = new LinkedHashMap<>();
        for (Movimiento movimiento : movimientos) {
            agrupados.computeIfAbsent(movimiento.getCuenta().getNumeroCuenta(), ignored -> new java.util.ArrayList<>())
                    .add(movimiento);
        }

        return agrupados.values().stream()
                .map(items -> {
                    Movimiento primero = items.get(0);
                    return new ReporteCuentaResponse(
                            primero.getCuenta().getClienteNombre(),
                            primero.getCuenta().getNumeroCuenta(),
                            primero.getCuenta().getTipoCuenta(),
                            primero.getCuenta().getSaldoInicial(),
                            primero.getCuenta().getEstado(),
                            items.stream()
                                    .map(movimiento -> new ReporteMovimientoResponse(
                                            movimiento.getFecha(),
                                            movimiento.getValor(),
                                            movimiento.getSaldo()
                                    ))
                                    .toList()
                    );
                })
                .toList();
    }
}
