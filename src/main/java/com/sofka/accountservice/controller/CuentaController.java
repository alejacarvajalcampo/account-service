package com.sofka.accountservice.controller;

import com.sofka.accountservice.dto.CuentaRequest;
import com.sofka.accountservice.dto.CuentaResponse;
import com.sofka.accountservice.mapper.CuentaMapper;
import com.sofka.accountservice.service.CuentaApplicationService;
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
@RequestMapping("/cuentas")
@Tag(name = "Cuentas", description = "CRUD de cuentas bancarias. Requiere referencia local del cliente sincronizada asincronamente.")
public class CuentaController {

    private final CuentaApplicationService cuentaService;
    private final CuentaMapper cuentaMapper;

    public CuentaController(CuentaApplicationService cuentaService, CuentaMapper cuentaMapper) {
        this.cuentaService = cuentaService;
        this.cuentaMapper = cuentaMapper;
    }

    @PostMapping
    @Operation(summary = "Crear cuenta", description = "Crea una cuenta para un cliente previamente sincronizado desde customer-service.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cuenta creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Regla de negocio o validacion", content = @Content(schema = @Schema(implementation = com.sofka.accountservice.exception.ApiError.class),
                    examples = @ExampleObject(value = "{\"code\":\"REPORTE_INVALIDO\",\"message\":\"No existe referencia local para clienteId: 2\"}")))
    })
    public ResponseEntity<CuentaResponse> crear(@Valid @RequestBody CuentaRequest request) {
        CuentaResponse response = cuentaMapper.toResponse(cuentaService.crear(cuentaMapper.toEntity(request)));
        return ResponseEntity.created(URI.create("/cuentas/" + response.numeroCuenta())).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar cuentas", description = "Retorna cuentas paginadas. Soporta page, size y sort.")
    public ResponseEntity<Page<CuentaResponse>> listar(Pageable pageable) {
        return ResponseEntity.ok(cuentaService.listar(pageable).map(cuentaMapper::toResponse));
    }

    @GetMapping("/{numeroCuenta}")
    @Operation(summary = "Consultar cuenta por numero", description = "Obtiene el detalle de una cuenta por su numero unico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta encontrada"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada", content = @Content(schema = @Schema(implementation = com.sofka.accountservice.exception.ApiError.class),
                    examples = @ExampleObject(value = "{\"code\":\"CUENTA_NOT_FOUND\",\"message\":\"Cuenta no encontrada con numeroCuenta: 999999\"}")))
    })
    public ResponseEntity<CuentaResponse> obtener(@Parameter(description = "Numero de cuenta", example = "478758") @PathVariable Long numeroCuenta) {
        return ResponseEntity.ok(cuentaMapper.toResponse(cuentaService.obtenerPorNumeroCuenta(numeroCuenta)));
    }

    @PutMapping("/{numeroCuenta}")
    @Operation(summary = "Actualizar cuenta", description = "Actualiza una cuenta existente y recalcula sus saldos cuando aplica.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta actualizada"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida o regla de negocio", content = @Content(schema = @Schema(implementation = com.sofka.accountservice.exception.ApiError.class),
                    examples = @ExampleObject(value = "{\"code\":\"REPORTE_INVALIDO\",\"message\":\"No existe referencia local para clienteId: 2\"}"))),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada", content = @Content(schema = @Schema(implementation = com.sofka.accountservice.exception.ApiError.class),
                    examples = @ExampleObject(value = "{\"code\":\"CUENTA_NOT_FOUND\",\"message\":\"Cuenta no encontrada con numeroCuenta: 999999\"}")))
    })
    public ResponseEntity<CuentaResponse> actualizar(
            @Parameter(description = "Numero de cuenta", example = "478758") @PathVariable Long numeroCuenta,
            @Valid @RequestBody CuentaRequest request
    ) {
        return ResponseEntity.ok(cuentaMapper.toResponse(cuentaService.actualizar(numeroCuenta, request)));
    }

    @DeleteMapping("/{numeroCuenta}")
    @Operation(summary = "Eliminar cuenta", description = "Elimina una cuenta y sus movimientos asociados.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cuenta eliminada"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada", content = @Content(schema = @Schema(implementation = com.sofka.accountservice.exception.ApiError.class),
                    examples = @ExampleObject(value = "{\"code\":\"CUENTA_NOT_FOUND\",\"message\":\"Cuenta no encontrada con numeroCuenta: 999999\"}")))
    })
    public ResponseEntity<Void> eliminar(@Parameter(description = "Numero de cuenta", example = "478758") @PathVariable Long numeroCuenta) {
        cuentaService.eliminar(numeroCuenta);
        return ResponseEntity.noContent().build();
    }
}
