package com.sofka.accountservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sofka.accountservice.domain.Cuenta;
import com.sofka.accountservice.domain.Movimiento;
import com.sofka.accountservice.dto.MovimientoRequest;
import com.sofka.accountservice.exception.MovimientoNotFoundException;
import com.sofka.accountservice.repository.MovimientoRepository;
import com.sofka.accountservice.soporte.CuentaPruebaBuilder;
import com.sofka.accountservice.soporte.MovimientoRequestPruebaBuilder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    @Mock
    private CuentaApplicationService cuentaService;

    @InjectMocks
    private MovimientoService movimientoService;

    @Test
    void deberiaCrearMovimientoYRecalcularSaldoDeLaCuenta() {
        Cuenta cuenta = CuentaPruebaBuilder.unaCuenta()
                .conNumeroCuenta(900001L)
                .conSaldoDisponible(new BigDecimal("100.00"))
                .construir();
        MovimientoRequest request = MovimientoRequestPruebaBuilder.unMovimiento()
                .conNumeroCuenta(900001L)
                .conValor(new BigDecimal("200.00"))
                .construir();
        Movimiento guardado = new Movimiento();
        guardado.setMovimientoId(1L);
        Movimiento recargado = movimiento(1L, request.fecha(), request.tipoMovimiento(), request.valor(), cuenta, new BigDecimal("300.00"));
        ArgumentCaptor<Movimiento> movimientoCaptor = ArgumentCaptor.forClass(Movimiento.class);

        when(cuentaService.obtenerPorNumeroCuenta(900001L)).thenReturn(cuenta);
        when(movimientoRepository.save(argThat(movimiento ->
                movimiento.getFecha().equals(request.fecha())
                        && movimiento.getTipoMovimiento().equals(request.tipoMovimiento())
                        && movimiento.getValor().equals(request.valor())
                        && movimiento.getCuenta().equals(cuenta)
                        && movimiento.getSaldo().equals(cuenta.getSaldoDisponible())
        ))).thenReturn(guardado);
        when(movimientoRepository.findById(1L)).thenReturn(Optional.of(recargado));

        Movimiento resultado = movimientoService.crear(request);

        assertSame(recargado, resultado);
        verify(movimientoRepository).save(movimientoCaptor.capture());
        assertEquals(request.fecha(), movimientoCaptor.getValue().getFecha());
        assertEquals(request.tipoMovimiento(), movimientoCaptor.getValue().getTipoMovimiento());
        assertEquals(request.valor(), movimientoCaptor.getValue().getValor());
        assertEquals(cuenta, movimientoCaptor.getValue().getCuenta());
        assertEquals(new BigDecimal("100.00"), movimientoCaptor.getValue().getSaldo());
        verify(cuentaService).recalcularSaldos(900001L);
    }

    @Test
    void deberiaFallarAlCrearSiNoSePuedeRecuperarElMovimientoGuardado() {
        Cuenta cuenta = CuentaPruebaBuilder.unaCuenta().construir();
        MovimientoRequest request = MovimientoRequestPruebaBuilder.unMovimiento().construir();
        Movimiento guardado = new Movimiento();
        guardado.setMovimientoId(1L);

        when(cuentaService.obtenerPorNumeroCuenta(900001L)).thenReturn(cuenta);
        when(movimientoRepository.save(argThat(movimiento ->
                movimiento.getFecha().equals(request.fecha())
                        && movimiento.getTipoMovimiento().equals(request.tipoMovimiento())
                        && movimiento.getValor().equals(request.valor())
                        && movimiento.getCuenta().equals(cuenta)
        ))).thenReturn(guardado);
        when(movimientoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MovimientoNotFoundException.class, () -> movimientoService.crear(request));
    }

    @Test
    void deberiaActualizarMovimientoYRecalcularAmbasCuentasSiCambio() {
        Cuenta cuentaAnterior = CuentaPruebaBuilder.unaCuenta()
                .conNumeroCuenta(1L)
                .conSaldoDisponible(new BigDecimal("100.00"))
                .construir();
        Cuenta cuentaNueva = CuentaPruebaBuilder.unaCuenta()
                .conNumeroCuenta(2L)
                .conSaldoDisponible(new BigDecimal("500.00"))
                .construir();
        Movimiento existente = movimiento(
                8L,
                LocalDate.of(2022, 2, 10),
                "DEPOSITO",
                new BigDecimal("100.00"),
                cuentaAnterior,
                new BigDecimal("100.00")
        );
        Movimiento actualizado = movimiento(
                8L,
                LocalDate.of(2022, 2, 12),
                "RETIRO",
                new BigDecimal("-25.00"),
                cuentaNueva,
                new BigDecimal("475.00")
        );
        MovimientoRequest request = MovimientoRequestPruebaBuilder.unMovimiento()
                .conFecha(LocalDate.of(2022, 2, 12))
                .conTipoMovimiento("RETIRO")
                .conValor(new BigDecimal("-25.00"))
                .conNumeroCuenta(2L)
                .construir();

        when(movimientoRepository.findById(8L)).thenReturn(Optional.of(existente), Optional.of(actualizado));
        when(cuentaService.obtenerPorNumeroCuenta(2L)).thenReturn(cuentaNueva);
        when(movimientoRepository.save(existente)).thenReturn(existente);

        Movimiento resultado = movimientoService.actualizar(8L, request);

        assertSame(actualizado, resultado);
        assertEquals(cuentaNueva, existente.getCuenta());
        assertEquals(new BigDecimal("-25.00"), existente.getValor());
        verify(cuentaService).recalcularSaldos(2L);
        verify(cuentaService).recalcularSaldos(1L);
    }

    @Test
    void noDeberiaRecalcularLaCuentaAnteriorSiNoCambioLaCuentaDelMovimiento() {
        Cuenta cuenta = CuentaPruebaBuilder.unaCuenta()
                .conNumeroCuenta(1L)
                .conSaldoDisponible(new BigDecimal("100.00"))
                .construir();
        Movimiento existente = movimiento(
                8L,
                LocalDate.of(2022, 2, 10),
                "DEPOSITO",
                new BigDecimal("100.00"),
                cuenta,
                new BigDecimal("100.00")
        );
        MovimientoRequest request = MovimientoRequestPruebaBuilder.unMovimiento()
                .conNumeroCuenta(1L)
                .conValor(new BigDecimal("50.00"))
                .construir();

        when(movimientoRepository.findById(8L)).thenReturn(Optional.of(existente), Optional.of(existente));
        when(cuentaService.obtenerPorNumeroCuenta(1L)).thenReturn(cuenta);
        when(movimientoRepository.save(existente)).thenReturn(existente);

        movimientoService.actualizar(8L, request);

        verify(cuentaService).recalcularSaldos(1L);
        verify(cuentaService, never()).recalcularSaldos(2L);
    }

    @Test
    void deberiaEliminarMovimientoYRecalcularCuenta() {
        Cuenta cuenta = CuentaPruebaBuilder.unaCuenta().conNumeroCuenta(123L).construir();
        Movimiento movimiento = movimiento(
                5L,
                LocalDate.of(2022, 2, 10),
                "RETIRO",
                new BigDecimal("-10.00"),
                cuenta,
                new BigDecimal("90.00")
        );

        when(movimientoRepository.findById(5L)).thenReturn(Optional.of(movimiento));

        movimientoService.eliminar(5L);

        verify(movimientoRepository).delete(movimiento);
        verify(cuentaService).recalcularSaldos(123L);
    }

    private Movimiento movimiento(
            Long id,
            LocalDate fecha,
            String tipoMovimiento,
            BigDecimal valor,
            Cuenta cuenta,
            BigDecimal saldo
    ) {
        Movimiento movimiento = new Movimiento();
        movimiento.setMovimientoId(id);
        movimiento.setFecha(fecha);
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setValor(valor);
        movimiento.setCuenta(cuenta);
        movimiento.setSaldo(saldo);
        return movimiento;
    }
}
