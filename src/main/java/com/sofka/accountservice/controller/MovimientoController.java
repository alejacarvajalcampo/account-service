package com.sofka.accountservice.controller;

import com.sofka.accountservice.dto.MovimientoRequest;
import com.sofka.accountservice.dto.MovimientoResponse;
import com.sofka.accountservice.mapper.MovimientoMapper;
import com.sofka.accountservice.service.MovimientoApplicationService;
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
@RequestMapping("/movimientos")
public class MovimientoController {

    private final MovimientoApplicationService movimientoService;
    private final MovimientoMapper movimientoMapper;

    public MovimientoController(MovimientoApplicationService movimientoService, MovimientoMapper movimientoMapper) {
        this.movimientoService = movimientoService;
        this.movimientoMapper = movimientoMapper;
    }

    @PostMapping
    public ResponseEntity<MovimientoResponse> crear(@Valid @RequestBody MovimientoRequest request) {
        MovimientoResponse response = movimientoMapper.toResponse(movimientoService.crear(request));
        return ResponseEntity.created(URI.create("/movimientos/" + response.movimientoId())).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<MovimientoResponse>> listar(Pageable pageable) {
        return ResponseEntity.ok(movimientoService.listar(pageable).map(movimientoMapper::toResponse));
    }

    @GetMapping("/{movimientoId}")
    public ResponseEntity<MovimientoResponse> obtener(@PathVariable Long movimientoId) {
        return ResponseEntity.ok(movimientoMapper.toResponse(movimientoService.obtenerPorId(movimientoId)));
    }

    @PutMapping("/{movimientoId}")
    public ResponseEntity<MovimientoResponse> actualizar(
            @PathVariable Long movimientoId,
            @Valid @RequestBody MovimientoRequest request
    ) {
        return ResponseEntity.ok(movimientoMapper.toResponse(movimientoService.actualizar(movimientoId, request)));
    }

    @DeleteMapping("/{movimientoId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long movimientoId) {
        movimientoService.eliminar(movimientoId);
        return ResponseEntity.noContent().build();
    }
}
