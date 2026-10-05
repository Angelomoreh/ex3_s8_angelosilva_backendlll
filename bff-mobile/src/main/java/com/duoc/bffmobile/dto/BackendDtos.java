package com.duoc.bffmobile.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class BackendDtos {

    private BackendDtos() {
    }

    public record Cuenta(
            Long id,
            String nombre,
            BigDecimal saldo,
            Integer edad,
            String tipo,
            Boolean activa,
            String moneda
    ) {
    }

    public record Transaccion(
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

    public record Resumen(
            Long cuentaId,
            long cantidadMovimientos,
            BigDecimal totalCreditos,
            BigDecimal totalDebitos,
            long cantidadAnomalias
    ) {
    }
}
