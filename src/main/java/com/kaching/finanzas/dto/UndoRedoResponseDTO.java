package com.kaching.finanzas.dto;

import com.kaching.finanzas.model.enums.TipoAccionEnum;
import com.kaching.finanzas.model.enums.TipoEntidadEnum;

public record UndoRedoResponseDTO(
        TipoEntidadEnum tipoEntidad,   // GASTO o INGRESO
        Long entidadId,                // id del gasto/ingreso afectado
        TipoAccionEnum accionOriginal, // qué acción se revirtió (CREAR/EDITAR/ELIMINAR)
        String resultado,              // "RESTAURADO", "ELIMINADO", "EDITADO" — describe el efecto neto
        PresupuestoResponseDTO presupuestoActualizado // balance ya recalculado
) {}