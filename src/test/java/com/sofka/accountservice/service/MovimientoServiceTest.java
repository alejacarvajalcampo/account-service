package com.sofka.accountservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sofka.accountservice.domain.Cuenta;
import com.sofka.accountservice.dto.MovimientoRequest;
import com.sofka.accountservice.exception.SaldoNoDisponibleException;
import com.sofka.accountservice.repository.CuentaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
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
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(900001L);
        cuenta.setTipoCuenta("Ahorro");
        cuenta.setSaldoInicial(new BigDecimal("100.00"));
        cuenta.setSaldoDisponible(new BigDecimal("100.00"));
        cuenta.setEstado(true);
        cuenta.setClienteId(99L);
        cuenta.setClienteNombre("Cliente Prueba");
        cuentaRepository.save(cuenta);

        MovimientoRequest request = new MovimientoRequest(
                LocalDate.of(2022, 2, 12),
                "RETIRO",
                new BigDecimal("-3000.00"),
                900001L
        );

        assertThrows(SaldoNoDisponibleException.class, () -> movimientoService.crear(request));
    }

    @Test
    void shouldUpdateAvailableBalanceAfterDeposit() {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(900002L);
        cuenta.setTipoCuenta("Corriente");
        cuenta.setSaldoInicial(new BigDecimal("100.00"));
        cuenta.setSaldoDisponible(new BigDecimal("100.00"));
        cuenta.setEstado(true);
        cuenta.setClienteId(98L);
        cuenta.setClienteNombre("Cliente Dos");
        cuentaRepository.save(cuenta);

        MovimientoRequest request = new MovimientoRequest(
                LocalDate.of(2022, 2, 12),
                "DEPOSITO",
                new BigDecimal("200.00"),
                900002L
        );

        movimientoService.crear(request);
        Cuenta actualizada = cuentaRepository.findById(900002L).orElseThrow();

        assertEquals(new BigDecimal("300.00"), actualizada.getSaldoDisponible());
    }
}
