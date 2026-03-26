package com.sofka.accountservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sofka.accountservice.domain.ClienteReferencia;
import com.sofka.accountservice.domain.Cuenta;
import com.sofka.accountservice.domain.Movimiento;
import com.sofka.accountservice.dto.CuentaRequest;
import com.sofka.accountservice.exception.DuplicateCuentaException;
import com.sofka.accountservice.exception.ReporteInvalidoException;
import com.sofka.accountservice.exception.SaldoNoDisponibleException;
import com.sofka.accountservice.repository.ClienteReferenciaRepository;
import com.sofka.accountservice.repository.CuentaRepository;
import com.sofka.accountservice.repository.MovimientoRepository;
import com.sofka.accountservice.soporte.ClienteReferenciaPruebaBuilder;
import com.sofka.accountservice.soporte.CuentaPruebaBuilder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CuentaServiceTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private MovimientoRepository movimientoRepository;

    @Mock
    private ClienteReferenciaRepository clienteReferenciaRepository;

    @InjectMocks
    private CuentaService cuentaService;

    @Test
    void deberiaCrearCuentaSincronizandoClienteYSaldoDisponible() {
        Cuenta cuenta = CuentaPruebaBuilder.unaCuenta()
                .conSaldoDisponible(BigDecimal.ZERO)
                .conClienteId(99L)
                .construir();
        ClienteReferencia cliente = ClienteReferenciaPruebaBuilder.unClienteReferencia()
                .conClienteId(99L)
                .conNombre("Cliente Sincronizado")
                .construir();

        when(cuentaRepository.existsById(900001L)).thenReturn(false);
        when(clienteReferenciaRepository.findById(99L)).thenReturn(Optional.of(cliente));
        when(cuentaRepository.save(cuenta)).thenReturn(cuenta);

        Cuenta resultado = cuentaService.crear(cuenta);

        assertSame(cuenta, resultado);
        assertEquals(new BigDecimal("100.00"), resultado.getSaldoDisponible());
        assertEquals("Cliente Sincronizado", resultado.getClienteNombre());
        verify(cuentaRepository).save(cuenta);
    }

    @Test
    void deberiaRechazarCreacionCuandoLaCuentaYaExiste() {
        Cuenta cuenta = CuentaPruebaBuilder.unaCuenta().construir();
        when(cuentaRepository.existsById(900001L)).thenReturn(true);

        assertThrows(DuplicateCuentaException.class, () -> cuentaService.crear(cuenta));

        verify(clienteReferenciaRepository, never()).findById(99L);
        verify(cuentaRepository, never()).save(cuenta);
    }

    @Test
    void deberiaFallarSiNoExisteLaReferenciaDelClienteAlCrear() {
        Cuenta cuenta = CuentaPruebaBuilder.unaCuenta().construir();
        when(cuentaRepository.existsById(900001L)).thenReturn(false);
        when(clienteReferenciaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ReporteInvalidoException.class, () -> cuentaService.crear(cuenta));

        verify(cuentaRepository, never()).save(cuenta);
    }

    @Test
    void deberiaActualizarCuentaYRecalcularSaldos() {
        Cuenta cuenta = CuentaPruebaBuilder.unaCuenta()
                .conNumeroCuenta(123L)
                .conSaldoInicial(new BigDecimal("100.00"))
                .conSaldoDisponible(new BigDecimal("100.00"))
                .conClienteId(10L)
                .conClienteNombre("Cliente Inicial")
                .construir();
        ClienteReferencia cliente = ClienteReferenciaPruebaBuilder.unClienteReferencia()
                .conClienteId(20L)
                .conNombre("Cliente Actualizado")
                .construir();
        Movimiento deposito = movimiento(1L, LocalDate.of(2022, 2, 10), new BigDecimal("50.00"), cuenta);
        Movimiento retiro = movimiento(2L, LocalDate.of(2022, 2, 11), new BigDecimal("-10.00"), cuenta);
        CuentaRequest request = new CuentaRequest(123L, "Corriente", new BigDecimal("200.00"), false, 20L);

        when(cuentaRepository.findById(123L)).thenReturn(Optional.of(cuenta), Optional.of(cuenta));
        when(clienteReferenciaRepository.findById(20L)).thenReturn(Optional.of(cliente));
        when(movimientoRepository.findByCuentaNumeroCuentaOrderByFechaAscMovimientoIdAsc(123L))
                .thenReturn(List.of(deposito, retiro));
        when(cuentaRepository.save(cuenta)).thenReturn(cuenta);

        Cuenta resultado = cuentaService.actualizar(123L, request);

        assertSame(cuenta, resultado);
        assertEquals("Corriente", cuenta.getTipoCuenta());
        assertEquals(new BigDecimal("200.00"), cuenta.getSaldoInicial());
        assertEquals(new BigDecimal("240.00"), cuenta.getSaldoDisponible());
        assertEquals("Cliente Actualizado", cuenta.getClienteNombre());
        assertEquals(new BigDecimal("250.00"), deposito.getSaldo());
        assertEquals(new BigDecimal("240.00"), retiro.getSaldo());
        verify(movimientoRepository).saveAll(List.of(deposito, retiro));
        verify(cuentaRepository, times(2)).save(cuenta);
    }

    @Test
    void deberiaRechazarActualizacionSiElNumeroDeCuentaNoCoincide() {
        CuentaRequest request = new CuentaRequest(999L, "Ahorro", new BigDecimal("100.00"), true, 99L);

        assertThrows(DuplicateCuentaException.class, () -> cuentaService.actualizar(123L, request));

        verify(cuentaRepository, never()).findById(123L);
    }

    @Test
    void deberiaRechazarRecalculoCuandoElSaldoQuedaNegativo() {
        Cuenta cuenta = CuentaPruebaBuilder.unaCuenta()
                .conNumeroCuenta(123L)
                .conSaldoInicial(new BigDecimal("100.00"))
                .conSaldoDisponible(new BigDecimal("100.00"))
                .construir();
        Movimiento retiro = movimiento(1L, LocalDate.of(2022, 2, 10), new BigDecimal("-150.00"), cuenta);

        when(cuentaRepository.findById(123L)).thenReturn(Optional.of(cuenta));
        when(movimientoRepository.findByCuentaNumeroCuentaOrderByFechaAscMovimientoIdAsc(123L))
                .thenReturn(List.of(retiro));

        assertThrows(SaldoNoDisponibleException.class, () -> cuentaService.recalcularSaldos(123L));

        verify(movimientoRepository, never()).saveAll(List.of(retiro));
        verify(cuentaRepository, never()).save(cuenta);
    }

    @Test
    void deberiaEliminarCuentaExistente() {
        Cuenta cuenta = CuentaPruebaBuilder.unaCuenta().construir();
        when(cuentaRepository.findById(900001L)).thenReturn(Optional.of(cuenta));

        cuentaService.eliminar(900001L);

        verify(cuentaRepository).delete(cuenta);
    }

    private Movimiento movimiento(Long id, LocalDate fecha, BigDecimal valor, Cuenta cuenta) {
        Movimiento movimiento = new Movimiento();
        movimiento.setMovimientoId(id);
        movimiento.setFecha(fecha);
        movimiento.setTipoMovimiento(valor.signum() >= 0 ? "DEPOSITO" : "RETIRO");
        movimiento.setValor(valor);
        movimiento.setCuenta(cuenta);
        return movimiento;
    }
}
