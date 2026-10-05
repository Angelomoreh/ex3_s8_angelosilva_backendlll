package com.duoc.cuentasservice.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CuentaResponse(
        Long id,
        String nombre,
        BigDecimal saldo,
        Integer edad,
        String tipo,
        Boolean activa,
        String moneda,
        OffsetDateTime fechaActualizacion
) {
}
