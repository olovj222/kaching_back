package com.kaching.finanzas.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.kaching.finanzas.model.enums.EstadoPresupuestoEnum;
import com.kaching.finanzas.model.enums.FrecuenciaEnum;

public record PresupuestoResponseDTO(
        Long id,
        String nombre,
        Long montoInicial,
        FrecuenciaEnum frecuencia,
        Long montoRecurrente,
        LocalDateTime fechaCreacion,
        EstadoPresupuestoEnum estado,
        Long balanceActual
) {}
