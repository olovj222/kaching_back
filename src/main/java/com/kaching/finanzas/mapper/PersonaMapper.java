package com.kaching.finanzas.mapper;

import com.kaching.finanzas.dto.PersonaResponseDTO;
import com.kaching.finanzas.model.Persona;

public class PersonaMapper {

    private PersonaMapper() {}

    public static PersonaResponseDTO toResponseDTO(Persona persona) {
        if (persona == null) return null;

        return new PersonaResponseDTO(
                persona.getId(),
                persona.getNombre(),
                persona.getActivo()
        );
    }
}