package com.kaching.finanzas.service;

import com.kaching.finanzas.dto.IngresoRequestDTO;
import com.kaching.finanzas.dto.IngresoResponseDTO;
import com.kaching.finanzas.dto.snapshots.IngresoSnapshotDTO;
import com.kaching.finanzas.mapper.IngresoMapper;
import com.kaching.finanzas.mapper.SnapshotMapper;
import com.kaching.finanzas.model.Ingreso;
import com.kaching.finanzas.model.Persona;
import com.kaching.finanzas.model.Presupuesto;
import com.kaching.finanzas.model.enums.EstadoPresupuestoEnum;
import com.kaching.finanzas.model.enums.TipoAccionEnum;
import com.kaching.finanzas.model.enums.TipoEntidadEnum;
import com.kaching.finanzas.repository.IngresoRepository;
import com.kaching.finanzas.repository.PersonaRepository;
import com.kaching.finanzas.repository.PresupuestoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngresoService {

    private final IngresoRepository ingresoRepository;
    private final PresupuestoRepository presupuestoRepository;
    private final PersonaRepository personaRepository;
    private final HistorialService historialService;

    @Transactional(readOnly = true)
    public List<IngresoResponseDTO> listar(Long presupuestoId) {
        if (!presupuestoRepository.existsById(presupuestoId)) {
            throw new EntityNotFoundException("Presupuesto no encontrado: " + presupuestoId);
        }
        return ingresoRepository.findByPresupuestoIdAndEliminadoFalseOrderByFechaIngresoDesc(presupuestoId)
                .stream()
                .map(IngresoMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public IngresoResponseDTO crear(Long presupuestoId, IngresoRequestDTO dto) {
        Presupuesto presupuesto = obtenerPresupuestoActivo(presupuestoId);
        Persona persona = obtenerPersonaActiva(dto.personaId());

        Ingreso ingreso = ingresoRepository.save(Ingreso.builder()
                .presupuesto(presupuesto)
                .monto(dto.monto())
                .descripcion(dto.descripcion().trim())
                .persona(persona)
                .build());

        // "antes" = el mismo registro marcado como eliminado (todavia no existia)
        historialService.registrar(presupuesto, persona, TipoEntidadEnum.INGRESO, ingreso.getId(),
                TipoAccionEnum.CREAR,
                SnapshotMapper.toSnapshot(ingreso, true),
                SnapshotMapper.toSnapshot(ingreso));

        return IngresoMapper.toResponseDTO(ingreso);
    }

    // dto.personaId() es quien edita (dueño de la accion en el historial).
    // El ingreso conserva a la persona que lo agrego originalmente.
    @Transactional
    public IngresoResponseDTO editar(Long id, IngresoRequestDTO dto) {
        Ingreso ingreso = obtenerIngresoVigente(id);
        Presupuesto presupuesto = ingreso.getPresupuesto();
        verificarActivo(presupuesto);
        Persona actor = obtenerPersonaActiva(dto.personaId());

        String descripcion = dto.descripcion().trim();
        if (ingreso.getMonto().equals(dto.monto()) && ingreso.getDescripcion().equals(descripcion)) {
            return IngresoMapper.toResponseDTO(ingreso); // sin cambios: no ensucia el historial
        }

        IngresoSnapshotDTO antes = SnapshotMapper.toSnapshot(ingreso);
        ingreso.setMonto(dto.monto());
        ingreso.setDescripcion(descripcion);

        historialService.registrar(presupuesto, actor, TipoEntidadEnum.INGRESO, id,
                TipoAccionEnum.EDITAR, antes, SnapshotMapper.toSnapshot(ingreso));

        return IngresoMapper.toResponseDTO(ingreso);
    }

    // Soft delete: la fila sigue existiendo para que el undo pueda restaurarla.
    @Transactional
    public void eliminar(Long id, Long personaId) {
        Ingreso ingreso = obtenerIngresoVigente(id);
        Presupuesto presupuesto = ingreso.getPresupuesto();
        verificarActivo(presupuesto);
        Persona actor = obtenerPersonaActiva(personaId);

        IngresoSnapshotDTO antes = SnapshotMapper.toSnapshot(ingreso);
        ingreso.setEliminado(true);

        historialService.registrar(presupuesto, actor, TipoEntidadEnum.INGRESO, id,
                TipoAccionEnum.ELIMINAR, antes, SnapshotMapper.toSnapshot(ingreso));
    }

    private Ingreso obtenerIngresoVigente(Long id) {
        return ingresoRepository.findById(id)
                .filter(i -> !i.getEliminado())
                .orElseThrow(() -> new EntityNotFoundException("Ingreso no encontrado: " + id));
    }

    private Presupuesto obtenerPresupuestoActivo(Long id) {
        Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Presupuesto no encontrado: " + id));
        verificarActivo(presupuesto);
        return presupuesto;
    }

    private void verificarActivo(Presupuesto presupuesto) {
        if (presupuesto.getEstado() != EstadoPresupuestoEnum.ACTIVO) {
            throw new IllegalStateException("La hoja está archivada o eliminada; restáurala para modificarla");
        }
    }

    private Persona obtenerPersonaActiva(Long id) {
        return personaRepository.findById(id)
                .filter(Persona::getActivo)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada o inactiva: " + id));
    }
}
