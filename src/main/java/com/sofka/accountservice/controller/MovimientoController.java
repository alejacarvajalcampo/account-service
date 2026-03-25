package com.sofka.accountservice.controller;

import com.sofka.accountservice.dto.MovimientoRequest;
import com.sofka.accountservice.dto.MovimientoResponse;
import com.sofka.accountservice.mapper.MovimientoMapper;
import com.sofka.accountservice.service.MovimientoApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Movimientos", description = "CRUD de movimientos. Al registrar o actualizar recalcula el saldo disponible y valida saldo no disponible.")
public class MovimientoController {

    private final MovimientoApplicationService movimientoService;
    private final MovimientoMapper movimientoMapper;

    public MovimientoController(MovimientoApplicationService movimientoService, MovimientoMapper movimientoMapper) {
        this.movimientoService = movimientoService;
        this.movimientoMapper = movimientoMapper;
    }

    @PostMapping
    @Operation(summary = "Crear movimiento", description = "Registra un deposito o retiro y recalcula saldos de la cuenta.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Movimiento creado"),
            @ApiResponse(responseCode = "400", description = "Saldo no disponible o validacion", content = @Content(schema = @Schema(implementation = com.sofka.accountservice.exception.ApiError.class),
                    examples = @ExampleObject(value = "{\"code\":\"SALDO_NO_DISPONIBLE\",\"message\":\"Saldo no disponible\"}")))
    })
    public ResponseEntity<MovimientoResponse> crear(@Valid @RequestBody MovimientoRequest request) {
        MovimientoResponse response = movimientoMapper.toResponse(movimientoService.crear(request));
        return ResponseEntity.created(URI.create("/movimientos/" + response.movimientoId())).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar movimientos", description = "Retorna movimientos paginados. Soporta page, size y sort.")
    public ResponseEntity<Page<MovimientoResponse>> listar(Pageable pageable) {
        return ResponseEntity.ok(movimientoService.listar(pageable).map(movimientoMapper::toResponse));
    }

    @GetMapping("/{movimientoId}")
    @Operation(summary = "Consultar movimiento por id", description = "Obtiene un movimiento especifico por su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimiento encontrado"),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado", content = @Content(schema = @Schema(implementation = com.sofka.accountservice.exception.ApiError.class),
                    examples = @ExampleObject(value = "{\"code\":\"MOVIMIENTO_NOT_FOUND\",\"message\":\"Movimiento no encontrado con movimientoId: 99\"}")))
    })
    public ResponseEntity<MovimientoResponse> obtener(@Parameter(description = "Identificador del movimiento", example = "1") @PathVariable Long movimientoId) {
        return ResponseEntity.ok(movimientoMapper.toResponse(movimientoService.obtenerPorId(movimientoId)));
    }

    @PutMapping("/{movimientoId}")
    @Operation(summary = "Actualizar movimiento", description = "Actualiza un movimiento y recalcula los saldos involucrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimiento actualizado"),
            @ApiResponse(responseCode = "400", description = "Saldo no disponible o validacion", content = @Content(schema = @Schema(implementation = com.sofka.accountservice.exception.ApiError.class),
                    examples = @ExampleObject(value = "{\"code\":\"SALDO_NO_DISPONIBLE\",\"message\":\"Saldo no disponible\"}"))),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado", content = @Content(schema = @Schema(implementation = com.sofka.accountservice.exception.ApiError.class),
                    examples = @ExampleObject(value = "{\"code\":\"MOVIMIENTO_NOT_FOUND\",\"message\":\"Movimiento no encontrado con movimientoId: 99\"}")))
    })
    public ResponseEntity<MovimientoResponse> actualizar(
            @Parameter(description = "Identificador del movimiento", example = "1") @PathVariable Long movimientoId,
            @Valid @RequestBody MovimientoRequest request
    ) {
        return ResponseEntity.ok(movimientoMapper.toResponse(movimientoService.actualizar(movimientoId, request)));
    }

    @DeleteMapping("/{movimientoId}")
    @Operation(summary = "Eliminar movimiento", description = "Elimina un movimiento y recalcula el saldo disponible de la cuenta.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Movimiento eliminado"),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado", content = @Content(schema = @Schema(implementation = com.sofka.accountservice.exception.ApiError.class),
                    examples = @ExampleObject(value = "{\"code\":\"MOVIMIENTO_NOT_FOUND\",\"message\":\"Movimiento no encontrado con movimientoId: 99\"}")))
    })
    public ResponseEntity<Void> eliminar(@Parameter(description = "Identificador del movimiento", example = "1") @PathVariable Long movimientoId) {
        movimientoService.eliminar(movimientoId);
        return ResponseEntity.noContent().build();
    }
}
