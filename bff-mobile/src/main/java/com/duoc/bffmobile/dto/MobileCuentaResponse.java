package com.duoc.bffmobile.dto;

import java.math.BigDecimal;
import java.util.List;

public record MobileCuentaResponse(
        String canal,
        Long cuentaId,
        String nombre,
        String tipo,
        BigDecimal saldo,
        String moneda,
        long cantidadMovimientos,
        List<MobileMovimientoResponse> ultimosMovimientos
) {
}
