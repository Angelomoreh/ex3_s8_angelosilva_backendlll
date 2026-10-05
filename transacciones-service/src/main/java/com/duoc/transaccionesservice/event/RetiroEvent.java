package com.duoc.transaccionesservice.event;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record RetiroEvent(
        String eventoId,
        Long cuentaId,
        BigDecimal monto,
        String canal,
        OffsetDateTime fecha
) {
}
