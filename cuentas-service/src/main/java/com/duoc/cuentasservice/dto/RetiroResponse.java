package com.duoc.cuentasservice.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record RetiroResponse(
        Long cuentaId,
        BigDecimal montoRetirado,
        BigDecimal saldoAnterior,
        BigDecimal saldoActual,
        String moneda,
        String estado,
        OffsetDateTime fecha
) {
}
