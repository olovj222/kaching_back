package com.kaching.finanzas.controller;

import com.kaching.finanzas.dto.PersonaRequestDTO;
import com.kaching.finanzas.dto.PersonaResponseDTO;
import com.kaching.finanzas.service.PersonaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/personas")
@RequiredArgsConstructor
public class PersonaController {

    private final PersonaService personaService;

    // Alimenta el selector inicial "¿quién eres?"
    @GetMapping
    public List<PersonaResponseDTO> listar() {
        return personaService.listarActivas();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonaResponseDTO crear(@Valid @RequestBody PersonaRequestDTO dto) {
        return personaService.crear(dto);
    }
}
