package com.kaching.finanzas.dto;

public record PersonaResponseDTO(
        Long id,
        String nombre,
        Boolean activo
) {}