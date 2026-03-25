package com.sofka.accountservice.repository;

import com.sofka.accountservice.domain.Cuenta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    List<Cuenta> findByClienteId(Long clienteId);
}
