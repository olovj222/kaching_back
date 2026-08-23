package com.kaching.finanzas.service;

import com.kaching.finanzas.dto.PersonaRequestDTO;
import com.kaching.finanzas.dto.PersonaResponseDTO;
import com.kaching.finanzas.mapper.PersonaMapper;
import com.kaching.finanzas.model.Persona;
import com.kaching.finanzas.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonaService {

    private final PersonaRepository personaRepository;

    public List<PersonaResponseDTO> listarActivas() {
        return personaRepository.findByActivoTrue().stream()
                .map(PersonaMapper::toResponseDTO)
                .toList();
    }

    public PersonaResponseDTO crear(PersonaRequestDTO dto) {
        Persona persona = Persona.builder()
                .nombre(dto.nombre())
                .activo(true)
                .build();

        return PersonaMapper.toResponseDTO(personaRepository.save(persona));
    }
}