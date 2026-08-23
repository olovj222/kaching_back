package com.kaching.finanzas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IngresoResponseDTO(
        Long id,
        Long presupuestoId,
        Long monto,
        String descripcion,
        PersonaResponseDTO persona,
        LocalDateTime fechaIngreso
) {}