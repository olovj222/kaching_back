package com.kaching.finanzas.controller;

import com.kaching.finanzas.dto.GastoRequestDTO;
import com.kaching.finanzas.dto.GastoResponseDTO;
import com.kaching.finanzas.service.GastoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GastoController {

    private final GastoService gastoService;

    @GetMapping("/presupuestos/{presupuestoId}/gastos")
    public List<GastoResponseDTO> listar(@PathVariable Long presupuestoId) {
        return gastoService.listar(presupuestoId);
    }

    @PostMapping("/presupuestos/{presupuestoId}/gastos")
    @ResponseStatus(HttpStatus.CREATED)
    public GastoResponseDTO crear(@PathVariable Long presupuestoId,
                                  @Valid @RequestBody GastoRequestDTO dto) {
        return gastoService.crear(presupuestoId, dto);
    }

    // dto.personaId = quien edita
    @PutMapping("/gastos/{id}")
    public GastoResponseDTO editar(@PathVariable Long id,
                                   @Valid @RequestBody GastoRequestDTO dto) {
        return gastoService.editar(id, dto);
    }

    // DELETE no lleva body: quien elimina viaja como ?personaId=
    @DeleteMapping("/gastos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id, @RequestParam Long personaId) {
        gastoService.eliminar(id, personaId);
    }
}
