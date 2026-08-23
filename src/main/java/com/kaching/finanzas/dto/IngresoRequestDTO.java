package com.kaching.finanzas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;



public record IngresoRequestDTO(
        @NotNull(message = "El monto es obligatorio")
        @Positive(message = "El monto debe ser mayor a cero")
        Long monto,

        @NotBlank(message = "La descripción no puede estar vacía")
        String descripcion,

        @NotNull(message = "Debes indicar quién agrega el ingreso")
        Long personaId
) {}