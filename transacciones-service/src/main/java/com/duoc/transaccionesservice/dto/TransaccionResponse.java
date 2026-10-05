package com.duoc.transaccionesservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransaccionResponse(
        Long id,
        Long cuentaId,
        LocalDate fecha,
        String tipo,
        BigDecimal monto,
        String descripcion,
        String canal,
        Boolean anomalia
) {
}
