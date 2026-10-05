package com.duoc.bffweb.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record WebCuentaResponse(
        String canal,
        BackendDtos.Cuenta cuenta,
        BackendDtos.Resumen resumen,
        List<BackendDtos.Transaccion> transacciones,
        OffsetDateTime generadoEn
) {
}
