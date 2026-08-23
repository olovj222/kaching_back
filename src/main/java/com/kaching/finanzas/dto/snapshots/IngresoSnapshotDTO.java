package com.kaching.finanzas.dto.snapshots;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IngresoSnapshotDTO(
        Long id,
        Long presupuestoId,
        Long monto,
        String descripcion,
        Long personaId,
        LocalDateTime fechaIngreso,
        Boolean eliminado
) {}