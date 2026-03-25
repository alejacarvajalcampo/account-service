package com.sofka.accountservice.controller;

import com.sofka.accountservice.dto.CuentaRequest;
import com.sofka.accountservice.dto.CuentaResponse;
import com.sofka.accountservice.mapper.CuentaMapper;
import com.sofka.accountservice.service.CuentaApplicationService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cuentas")
public class CuentaController {

    private final CuentaApplicationService cuentaService;
    private final CuentaMapper cuentaMapper;

    public CuentaController(CuentaApplicationService cuentaService, CuentaMapper cuentaMapper) {
        this.cuentaService = cuentaService;
        this.cuentaMapper = cuentaMapper;
    }

    @PostMapping
    public ResponseEntity<CuentaResponse> crear(@Valid @RequestBody CuentaRequest request) {
        CuentaResponse response = cuentaMapper.toResponse(cuentaService.crear(cuentaMapper.toEntity(request)));
        return ResponseEntity.created(URI.create("/cuentas/" + response.numeroCuenta())).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<CuentaResponse>> listar(Pageable pageable) {
        return ResponseEntity.ok(cuentaService.listar(pageable).map(cuentaMapper::toResponse));
    }

    @GetMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaResponse> obtener(@PathVariable Long numeroCuenta) {
        return ResponseEntity.ok(cuentaMapper.toResponse(cuentaService.obtenerPorNumeroCuenta(numeroCuenta)));
    }

    @PutMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaResponse> actualizar(
            @PathVariable Long numeroCuenta,
            @Valid @RequestBody CuentaRequest request
    ) {
        return ResponseEntity.ok(cuentaMapper.toResponse(cuentaService.actualizar(numeroCuenta, request)));
    }

    @DeleteMapping("/{numeroCuenta}")
    public ResponseEntity<Void> eliminar(@PathVariable Long numeroCuenta) {
        cuentaService.eliminar(numeroCuenta);
        return ResponseEntity.noContent().build();
    }
}
