package com.kaching.finanzas.mapper;

import com.kaching.finanzas.dto.IngresoResponseDTO;
import com.kaching.finanzas.model.Ingreso;

public class IngresoMapper {

    private IngresoMapper() {}

    public static IngresoResponseDTO toResponseDTO(Ingreso ingreso) {
        if (ingreso == null) return null;

        return new IngresoResponseDTO(
                ingreso.getId(),
                ingreso.getPresupuesto().getId(),
                ingreso.getMonto(),
                ingreso.getDescripcion(),
                PersonaMapper.toResponseDTO(ingreso.getPersona()),
                ingreso.getFechaIngreso()
        );
    }
}