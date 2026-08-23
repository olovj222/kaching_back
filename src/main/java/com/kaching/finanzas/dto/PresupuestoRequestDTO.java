package com.kaching.finanzas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

import com.kaching.finanzas.model.enums.FrecuenciaEnum;

public record PresupuestoRequestDTO(
        @NotBlank(message = "El nombre no puede estar vacío")
        String nombre,

        @NotNull(message = "El monto inicial es obligatorio")
        @PositiveOrZero(message = "El monto inicial no puede ser negativo")
        Long montoInicial,

        @NotNull(message = "La frecuencia es obligatoria")
        FrecuenciaEnum frecuencia,

        @PositiveOrZero(message = "El monto recurrente no puede ser negativo")
        Long montoRecurrente
) {}