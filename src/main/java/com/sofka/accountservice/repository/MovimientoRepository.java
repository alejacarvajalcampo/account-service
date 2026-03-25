package com.sofka.accountservice.repository;

import com.sofka.accountservice.domain.Movimiento;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByCuentaNumeroCuentaOrderByFechaAscMovimientoIdAsc(Long numeroCuenta);

    List<Movimiento> findByCuentaClienteIdAndFechaBetweenOrderByFechaAscMovimientoIdAsc(
            Long clienteId,
            LocalDate fechaDesde,
            LocalDate fechaHasta
    );
}
