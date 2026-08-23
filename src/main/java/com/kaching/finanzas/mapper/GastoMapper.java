package com.kaching.finanzas.mapper;

import com.kaching.finanzas.dto.GastoResponseDTO;
import com.kaching.finanzas.model.Gasto;

public class GastoMapper {

    private GastoMapper() {}

    public static GastoResponseDTO toResponseDTO(Gasto gasto) {
        if (gasto == null) return null;

        return new GastoResponseDTO(
                gasto.getId(),
                gasto.getPresupuesto().getId(),
                gasto.getMonto(),
                gasto.getDescripcion(),
                PersonaMapper.toResponseDTO(gasto.getPersona()),
                gasto.getFechaGasto()
        );
    }
}