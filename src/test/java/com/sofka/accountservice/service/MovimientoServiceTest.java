package com.sofka.accountservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sofka.accountservice.domain.Cuenta;
import com.sofka.accountservice.dto.MovimientoRequest;
import com.sofka.accountservice.exception.SaldoNoDisponibleException;
import com.sofka.accountservice.repository.CuentaRepository;
import com.sofka.accountservice.soporte.CuentaPruebaBuilder;
import com.sofka.accountservice.soporte.MovimientoRequestPruebaBuilder;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MovimientoServiceTest {

    @Autowired
    private MovimientoService movimientoService;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Test
    void deberiaRechazarRetiroCuandoElSaldoEsInsuficiente() {
        Cuenta cuenta = CuentaPruebaBuilder.unaCuenta().construir();
        cuentaRepository.save(cuenta);

        MovimientoRequest request = MovimientoRequestPruebaBuilder.unMovimiento()
                .conTipoMovimiento("RETIRO")
                .conValor(new BigDecimal("-3000.00"))
                .conNumeroCuenta(900001L)
                .construir();

        assertThrows(SaldoNoDisponibleException.class, () -> movimientoService.crear(request));
    }

    @Test
    void deberiaActualizarSaldoDisponibleDespuesDeUnDeposito() {
        Cuenta cuenta = CuentaPruebaBuilder.unaCuenta()
                .conNumeroCuenta(900002L)
                .conTipoCuenta("Corriente")
                .conClienteId(98L)
                .conClienteNombre("Cliente Dos")
                .construir();
        cuentaRepository.save(cuenta);

        MovimientoRequest request = MovimientoRequestPruebaBuilder.unMovimiento()
                .conNumeroCuenta(900002L)
                .construir();

        movimientoService.crear(request);
        Cuenta actualizada = cuentaRepository.findById(900002L).orElseThrow();

        assertEquals(new BigDecimal("300.00"), actualizada.getSaldoDisponible());
    }
}