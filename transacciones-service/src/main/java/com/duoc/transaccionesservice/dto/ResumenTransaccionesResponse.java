package com.duoc.transaccionesservice.dto;

import java.math.BigDecimal;

public record ResumenTransaccionesResponse(
        Long cuentaId,
        long cantidadMovimientos,
        BigDecimal totalCreditos,
        BigDecimal totalDebitos,
        long cantidadAnomalias
) {
}
