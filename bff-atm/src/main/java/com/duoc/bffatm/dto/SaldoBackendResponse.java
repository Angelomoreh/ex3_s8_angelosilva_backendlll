package com.duoc.bffatm.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record SaldoBackendResponse(
        Long cuentaId,
        BigDecimal saldo,
        String moneda,
        OffsetDateTime consultadoEn
) {
}
