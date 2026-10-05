package com.duoc.bffatm.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record RetiroBackendResponse(
        Long cuentaId,
        BigDecimal montoRetirado,
        BigDecimal saldoAnterior,
        BigDecimal saldoActual,
        String moneda,
        String estado,
        OffsetDateTime fecha
) {
}
