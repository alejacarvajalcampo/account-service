package com.sofka.accountservice.controller;

import com.sofka.accountservice.dto.ReporteCuentaResponse;
import com.sofka.accountservice.service.ReporteApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reportes")
@Tag(name = "Reportes", description = "Consultas de estado de cuenta por cliente y rango de fechas.")
public class ReporteController {

    private final ReporteApplicationService reporteService;

    public ReporteController(ReporteApplicationService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    @Operation(summary = "Generar reporte de estado de cuenta", description = "Retorna en JSON el detalle de movimientos por cliente dentro de un rango de fechas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reporte generado"),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos", content = @Content(schema = @Schema(implementation = com.sofka.accountservice.exception.ApiError.class),
                    examples = @ExampleObject(value = "{\"code\":\"REPORTE_INVALIDO\",\"message\":\"fechaDesde no puede ser mayor que fechaHasta\"}")))
    })
    public ResponseEntity<List<ReporteCuentaResponse>> generar(
            @Parameter(description = "Fecha inicial del reporte", example = "2022-02-08") @RequestParam("fechaDesde") LocalDate fechaDesde,
            @Parameter(description = "Fecha final del reporte", example = "2022-02-10") @RequestParam("fechaHasta") LocalDate fechaHasta,
            @Parameter(description = "clienteId sobre el cual consultar el reporte", example = "2") @RequestParam("clienteId") Long clienteId
    ) {
        return ResponseEntity.ok(reporteService.generarReporte(fechaDesde, fechaHasta, clienteId));
    }
}
