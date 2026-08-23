package com.kaching.finanzas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record GastoRequestDTO(
        @NotNull(message = "El monto es obligatorio")
        @Positive(message = "El monto debe ser mayor a cero")
        Long monto,

        @NotBlank(message = "La descripción no puede estar vacía")
        String descripcion,

        @NotNull(message = "Debes indicar quién anota el gasto")
        Long personaId
) {}