package com.kaching.finanzas.dto;

import jakarta.validation.constraints.NotBlank;

public record PersonaRequestDTO(
        @NotBlank(message = "El nombre no puede estar vacío")
        String nombre
) {}
