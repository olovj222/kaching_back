package com.kaching.finanzas.controller;

import com.kaching.finanzas.dto.PresupuestoRequestDTO;
import com.kaching.finanzas.dto.PresupuestoResponseDTO;
import com.kaching.finanzas.model.enums.EstadoPresupuestoEnum;
import com.kaching.finanzas.service.PresupuestoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/presupuestos")
@RequiredArgsConstructor
public class PresupuestoController {

    private final PresupuestoService presupuestoService;

    // ?estado=ACTIVO (por defecto) | ARCHIVADO (papelera)
    @GetMapping
    public List<PresupuestoResponseDTO> listar(
            @RequestParam(defaultValue = "ACTIVO") EstadoPresupuestoEnum estado) {
        return presupuestoService.listarPorEstado(estado);
    }

    @GetMapping("/{id}")
    public PresupuestoResponseDTO obtener(@PathVariable Long id) {
        return presupuestoService.obtenerPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PresupuestoResponseDTO crear(@Valid @RequestBody PresupuestoRequestDTO dto) {
        return presupuestoService.crear(dto);
    }

    @PutMapping("/{id}")
    public PresupuestoResponseDTO editar(@PathVariable Long id,
                                         @Valid @RequestBody PresupuestoRequestDTO dto) {
        return presupuestoService.editar(id, dto);
    }

    @PatchMapping("/{id}/archivar")
    public PresupuestoResponseDTO archivar(@PathVariable Long id) {
        return presupuestoService.archivar(id);
    }

    @PatchMapping("/{id}/restaurar")
    public PresupuestoResponseDTO restaurar(@PathVariable Long id) {
        return presupuestoService.restaurar(id);
    }

    // Borrado definitivo: solo permitido si la hoja esta archivada (papelera)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarDefinitivo(@PathVariable Long id) {
        presupuestoService.eliminarDefinitivo(id);
    }
}
