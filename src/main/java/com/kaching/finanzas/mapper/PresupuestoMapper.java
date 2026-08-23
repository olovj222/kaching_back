package com.kaching.finanzas.mapper;


import com.kaching.finanzas.dto.PresupuestoResponseDTO;
import com.kaching.finanzas.model.Presupuesto;

public class PresupuestoMapper {

    private PresupuestoMapper() {}

    // El balance no vive en la entidad, así que se lo pasamos ya calculado desde el service
    public static PresupuestoResponseDTO toResponseDTO(Presupuesto presupuesto, Long balanceActual) {
        if (presupuesto == null) return null;

        return new PresupuestoResponseDTO(
                presupuesto.getId(),
                presupuesto.getNombre(),
                presupuesto.getMontoInicial(),
                presupuesto.getFrecuencia(),
                presupuesto.getMontoRecurrente(),
                presupuesto.getFechaCreacion(),
                presupuesto.getEstado(),
                balanceActual
        );
    }
}
