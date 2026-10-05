package com.duoc.bffatm.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record AtmRetiroResponse(
        String canal,
        Long cuentaId,
        BigDecimal montoRetirado,
        BigDecimal saldoDisponible,
        String moneda,
        String estado,
        OffsetDateTime fecha
) {
}
