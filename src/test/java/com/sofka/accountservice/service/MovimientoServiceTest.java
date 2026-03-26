package com.sofka.accountservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sofka.accountservice.domain.Cuenta;
import com.sofka.accountservice.dto.MovimientoRequest;
import com.sofka.accountservice.exception.SaldoNoDisponibleException;
import com.sofka.accountservice.repository.CuentaRepository;
import com.sofka.accountservice.support.CuentaTestDataBuilder;
import com.sofka.accountservice.support.MovimientoRequestTestDataBuilder;
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
    void shouldRejectWithdrawalWhenBalanceIsInsufficient() {
        Cuenta cuenta = CuentaTestDataBuilder.unaCuenta().build();
        cuentaRepository.save(cuenta);

        MovimientoRequest request = MovimientoRequestTestDataBuilder.unMovimiento()
                .conTipoMovimiento("RETIRO")
                .conValor(new BigDecimal("-3000.00"))
                .conNumeroCuenta(900001L)
                .build();

        assertThrows(SaldoNoDisponibleException.class, () -> movimientoService.crear(request));
    }

    @Test
    void shouldUpdateAvailableBalanceAfterDeposit() {
        Cuenta cuenta = CuentaTestDataBuilder.unaCuenta()
                .conNumeroCuenta(900002L)
                .conTipoCuenta("Corriente")
                .conClienteId(98L)
                .conClienteNombre("Cliente Dos")
                .build();
        cuentaRepository.save(cuenta);

        MovimientoRequest request = MovimientoRequestTestDataBuilder.unMovimiento()
                .conNumeroCuenta(900002L)
                .build();

        movimientoService.crear(request);
        Cuenta actualizada = cuentaRepository.findById(900002L).orElseThrow();

        assertEquals(new BigDecimal("300.00"), actualizada.getSaldoDisponible());
    }
}