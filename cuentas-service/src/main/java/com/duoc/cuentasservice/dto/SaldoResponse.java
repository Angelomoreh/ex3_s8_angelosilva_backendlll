package com.duoc.cuentasservice.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record SaldoResponse(
        Long cuentaId,
        BigDecimal saldo,
        String moneda,
        OffsetDateTime consultadoEn
) {
}
