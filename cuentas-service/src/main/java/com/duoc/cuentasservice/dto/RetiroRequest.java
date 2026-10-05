package com.duoc.cuentasservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RetiroRequest(
        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "1.00", message = "El monto debe ser mayor o igual a 1")
        BigDecimal monto
) {
}
