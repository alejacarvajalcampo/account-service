package com.sofka.accountservice.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
        LocalDateTime timestamp,
        int status,
        String code,
        String error,
        String message,
        List<String> details
) {
}
