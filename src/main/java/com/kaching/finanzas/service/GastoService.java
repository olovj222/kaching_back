package com.kaching.finanzas.service;

import com.kaching.finanzas.dto.GastoRequestDTO;
import com.kaching.finanzas.dto.GastoResponseDTO;
import com.kaching.finanzas.dto.snapshots.GastoSnapshotDTO;
import com.kaching.finanzas.mapper.GastoMapper;
import com.kaching.finanzas.mapper.SnapshotMapper;
import com.kaching.finanzas.model.Gasto;
import com.kaching.finanzas.model.Persona;
import com.kaching.finanzas.model.Presupuesto;
import com.kaching.finanzas.model.enums.EstadoPresupuestoEnum;
import com.kaching.finanzas.model.enums.TipoAccionEnum;
import com.kaching.finanzas.model.enums.TipoEntidadEnum;
import com.kaching.finanzas.repository.GastoRepository;
import com.kaching.finanzas.repository.PersonaRepository;
import com.kaching.finanzas.repository.PresupuestoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GastoService {

    private final GastoRepository gastoRepository;
    private final PresupuestoRepository presupuestoRepository;
    private final PersonaRepository personaRepository;
    private final HistorialService historialService;

    @Transactional(readOnly = true)
    public List<GastoResponseDTO> listar(Long presupuestoId) {
        if (!presupuestoRepository.existsById(presupuestoId)) {
            throw new EntityNotFoundException("Presupuesto no encontrado: " + presupuestoId);
        }
        return gastoRepository.findByPresupuestoIdAndEliminadoFalseOrderByFechaGastoDesc(presupuestoId)
                .stream()
                .map(GastoMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public GastoResponseDTO crear(Long presupuestoId, GastoRequestDTO dto) {
        Presupuesto presupuesto = obtenerPresupuestoActivo(presupuestoId);
        Persona persona = obtenerPersonaActiva(dto.personaId());

        Gasto gasto = gastoRepository.save(Gasto.builder()
                .presupuesto(presupuesto)
                .monto(dto.monto())
                .descripcion(dto.descripcion().trim())
                .persona(persona)
                .build());

        // "antes" = el mismo registro marcado como eliminado (todavia no existia)
        historialService.registrar(presupuesto, persona, TipoEntidadEnum.GASTO, gasto.getId(),
                TipoAccionEnum.CREAR,
                SnapshotMapper.toSnapshot(gasto, true),
                SnapshotMapper.toSnapshot(gasto));

        return GastoMapper.toResponseDTO(gasto);
    }

    // dto.personaId() es quien edita (dueño de la accion en el historial).
    // El gasto conserva a la persona que lo anoto originalmente.
    @Transactional
    public GastoResponseDTO editar(Long id, GastoRequestDTO dto) {
        Gasto gasto = obtenerGastoVigente(id);
        Presupuesto presupuesto = gasto.getPresupuesto();
        verificarActivo(presupuesto);
        Persona actor = obtenerPersonaActiva(dto.personaId());

        String descripcion = dto.descripcion().trim();
        if (gasto.getMonto().equals(dto.monto()) && gasto.getDescripcion().equals(descripcion)) {
            return GastoMapper.toResponseDTO(gasto); // sin cambios: no ensucia el historial
        }

        GastoSnapshotDTO antes = SnapshotMapper.toSnapshot(gasto);
        gasto.setMonto(dto.monto());
        gasto.setDescripcion(descripcion);

        historialService.registrar(presupuesto, actor, TipoEntidadEnum.GASTO, id,
                TipoAccionEnum.EDITAR, antes, SnapshotMapper.toSnapshot(gasto));

        return GastoMapper.toResponseDTO(gasto);
    }

    // Soft delete: la fila sigue existiendo para que el undo pueda restaurarla.
    @Transactional
    public void eliminar(Long id, Long personaId) {
        Gasto gasto = obtenerGastoVigente(id);
        Presupuesto presupuesto = gasto.getPresupuesto();
        verificarActivo(presupuesto);
        Persona actor = obtenerPersonaActiva(personaId);

        GastoSnapshotDTO antes = SnapshotMapper.toSnapshot(gasto);
        gasto.setEliminado(true);

        historialService.registrar(presupuesto, actor, TipoEntidadEnum.GASTO, id,
                TipoAccionEnum.ELIMINAR, antes, SnapshotMapper.toSnapshot(gasto));
    }

    private Gasto obtenerGastoVigente(Long id) {
        return gastoRepository.findById(id)
                .filter(g -> !g.getEliminado())
                .orElseThrow(() -> new EntityNotFoundException("Gasto no encontrado: " + id));
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
