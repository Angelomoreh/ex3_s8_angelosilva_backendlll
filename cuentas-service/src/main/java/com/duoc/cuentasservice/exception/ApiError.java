package com.duoc.cuentasservice.exception;

import java.time.OffsetDateTime;

public record ApiError(
        int estado,
        String error,
        String mensaje,
        String ruta,
        OffsetDateTime fecha
) {
}
