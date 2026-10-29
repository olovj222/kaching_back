package com.kaching.finanzas.controller;

import com.kaching.finanzas.dto.IngresoRequestDTO;
import com.kaching.finanzas.dto.IngresoResponseDTO;
import com.kaching.finanzas.service.IngresoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class IngresoController {

    private final IngresoService ingresoService;

    @GetMapping("/presupuestos/{presupuestoId}/ingresos")
    public List<IngresoResponseDTO> listar(@PathVariable Long presupuestoId) {
        return ingresoService.listar(presupuestoId);
    }

    @PostMapping("/presupuestos/{presupuestoId}/ingresos")
    @ResponseStatus(HttpStatus.CREATED)
    public IngresoResponseDTO crear(@PathVariable Long presupuestoId,
                                    @Valid @RequestBody IngresoRequestDTO dto) {
        return ingresoService.crear(presupuestoId, dto);
    }

    // dto.personaId = quien edita
    @PutMapping("/ingresos/{id}")
    public IngresoResponseDTO editar(@PathVariable Long id,
                                     @Valid @RequestBody IngresoRequestDTO dto) {
        return ingresoService.editar(id, dto);
    }

    // DELETE no lleva body: quien elimina viaja como ?personaId=
    @DeleteMapping("/ingresos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id, @RequestParam Long personaId) {
        ingresoService.eliminar(id, personaId);
    }
}
