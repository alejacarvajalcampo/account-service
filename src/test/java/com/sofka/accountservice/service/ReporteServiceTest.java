package com.sofka.accountservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sofka.accountservice.domain.Cuenta;
import com.sofka.accountservice.domain.Movimiento;
import com.sofka.accountservice.dto.ReporteCuentaResponse;
import com.sofka.accountservice.exception.ReporteInvalidoException;
import com.sofka.accountservice.repository.MovimientoRepository;
import com.sofka.accountservice.soporte.CuentaPruebaBuilder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    @InjectMocks
    private ReporteService reporteService;

    @Test
    void deberiaRechazarReporteSinClienteId() {
        LocalDate fechaDesde = LocalDate.of(2022, 2, 1);
        LocalDate fechaHasta = LocalDate.of(2022, 2, 28);

        assertThrows(ReporteInvalidoException.class, () -> reporteService.generarReporte(fechaDesde, fechaHasta, null));
    }

    @Test
    void deberiaRechazarReporteConRangoDeFechasInvalido() {
        LocalDate fechaDesde = LocalDate.of(2022, 3, 1);
        LocalDate fechaHasta = LocalDate.of(2022, 2, 28);

        assertThrows(ReporteInvalidoException.class, () -> reporteService.generarReporte(fechaDesde, fechaHasta, 1L));
    }

    @Test
    void deberiaAgruparMovimientosPorCuentaEnElReporte() {
        LocalDate fechaDesde = LocalDate.of(2022, 2, 1);
        LocalDate fechaHasta = LocalDate.of(2022, 2, 28);
        Cuenta cuentaUno = CuentaPruebaBuilder.unaCuenta()
                .conNumeroCuenta(1L)
                .conTipoCuenta("Ahorro")
                .conSaldoInicial(new BigDecimal("100.00"))
                .conClienteNombre("Jose Lema")
                .construir();
        Cuenta cuentaDos = CuentaPruebaBuilder.unaCuenta()
                .conNumeroCuenta(2L)
                .conTipoCuenta("Corriente")
                .conSaldoInicial(new BigDecimal("500.00"))
                .conClienteNombre("Jose Lema")
                .construir();
        List<Movimiento> movimientos = List.of(
                movimiento(1L, fechaDesde.plusDays(1), new BigDecimal("50.00"), new BigDecimal("150.00"), cuentaUno),
                movimiento(2L, fechaDesde.plusDays(2), new BigDecimal("-25.00"), new BigDecimal("125.00"), cuentaUno),
                movimiento(3L, fechaDesde.plusDays(3), new BigDecimal("100.00"), new BigDecimal("600.00"), cuentaDos)
        );

        when(movimientoRepository.findByCuentaClienteIdAndFechaBetweenOrderByFechaAscMovimientoIdAsc(99L, fechaDesde, fechaHasta))
                .thenReturn(movimientos);

        List<ReporteCuentaResponse> reporte = reporteService.generarReporte(fechaDesde, fechaHasta, 99L);

        assertEquals(2, reporte.size());
        assertEquals(1L, reporte.get(0).numeroCuenta());
        assertEquals(2, reporte.get(0).movimientos().size());
        assertEquals(new BigDecimal("125.00"), reporte.get(0).movimientos().get(1).saldoDisponible());
        assertEquals(2L, reporte.get(1).numeroCuenta());
        assertEquals(1, reporte.get(1).movimientos().size());
        verify(movimientoRepository)
                .findByCuentaClienteIdAndFechaBetweenOrderByFechaAscMovimientoIdAsc(99L, fechaDesde, fechaHasta);
    }

    private Movimiento movimiento(Long id, LocalDate fecha, BigDecimal valor, BigDecimal saldo, Cuenta cuenta) {
        Movimiento movimiento = new Movimiento();
        movimiento.setMovimientoId(id);
        movimiento.setFecha(fecha);
        movimiento.setTipoMovimiento(valor.signum() >= 0 ? "DEPOSITO" : "RETIRO");
        movimiento.setValor(valor);
        movimiento.setSaldo(saldo);
        movimiento.setCuenta(cuenta);
        return movimiento;
    }
}
