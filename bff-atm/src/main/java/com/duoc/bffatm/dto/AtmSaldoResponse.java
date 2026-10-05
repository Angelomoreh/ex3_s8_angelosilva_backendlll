package com.duoc.bffatm.dto;

import java.math.BigDecimal;

public record AtmSaldoResponse(
        String canal,
        Long cuentaId,
        BigDecimal saldoDisponible,
        String moneda
) {
}
