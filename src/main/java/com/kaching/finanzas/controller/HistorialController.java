package com.kaching.finanzas.controller;

import com.kaching.finanzas.dto.UndoRedoResponseDTO;
import com.kaching.finanzas.service.HistorialService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/presupuestos/{presupuestoId}")
@RequiredArgsConstructor
public class HistorialController {

    private final HistorialService historialService;

    // Ctrl+Z: deshace la ultima accion de esa persona en esta hoja
    @PostMapping("/undo")
    public UndoRedoResponseDTO deshacer(@PathVariable Long presupuestoId,
                                        @RequestParam Long personaId) {
        return historialService.deshacer(presupuestoId, personaId);
    }

    // Ctrl+Shift+Z: rehace lo ultimo que esa persona deshizo en esta hoja
    @PostMapping("/redo")
    public UndoRedoResponseDTO rehacer(@PathVariable Long presupuestoId,
                                       @RequestParam Long personaId) {
        return historialService.rehacer(presupuestoId, personaId);
    }
}
