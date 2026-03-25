package com.sofka.accountservice.exception;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            CuentaNotFoundException.class,
            MovimientoNotFoundException.class
    })
    public ResponseEntity<ApiError> handleNotFound(RuntimeException exception) {
        String code = exception instanceof CuentaNotFoundException ? "CUENTA_NOT_FOUND" : "MOVIMIENTO_NOT_FOUND";
        return buildResponse(HttpStatus.NOT_FOUND, code, exception.getMessage(), List.of());
    }

    @ExceptionHandler({
            DuplicateCuentaException.class,
            SaldoNoDisponibleException.class,
            ReporteInvalidoException.class
    })
    public ResponseEntity<ApiError> handleBusiness(RuntimeException exception) {
        String code;
        if (exception instanceof DuplicateCuentaException) {
            code = "CUENTA_DUPLICATE";
        } else if (exception instanceof SaldoNoDisponibleException) {
            code = "SALDO_NO_DISPONIBLE";
        } else {
            code = "REPORTE_INVALIDO";
        }
        return buildResponse(HttpStatus.BAD_REQUEST, code, exception.getMessage(), List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        List<String> details = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .toList();
        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "La solicitud contiene datos inválidos", details);
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }

    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String code, String message, List<String> details) {
        return ResponseEntity.status(status)
                .body(new ApiError(LocalDateTime.now(), status.value(), code, status.getReasonPhrase(), message, details));
    }
}
