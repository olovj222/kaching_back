package com.kaching.finanzas.service;

import com.kaching.finanzas.dto.PresupuestoRequestDTO;
import com.kaching.finanzas.dto.PresupuestoResponseDTO;
import com.kaching.finanzas.mapper.PresupuestoMapper;
import com.kaching.finanzas.model.Presupuesto;
import com.kaching.finanzas.model.enums.EstadoPresupuestoEnum;
import com.kaching.finanzas.repository.GastoRepository;
import com.kaching.finanzas.repository.IngresoRepository;
import com.kaching.finanzas.repository.PresupuestoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PresupuestoService {

    private final PresupuestoRepository presupuestoRepository;
    private final GastoRepository gastoRepository;
    private final IngresoRepository ingresoRepository;

    public Long calcularBalance(Long presupuestoId) {
        Presupuesto presupuesto = presupuestoRepository.findById(presupuestoId)
                .orElseThrow(() -> new EntityNotFoundException("Presupuesto no encontrado: " + presupuestoId));

        Long totalGastos = gastoRepository.sumMontoByPresupuestoId(presupuestoId);
        Long totalIngresos = ingresoRepository.sumMontoByPresupuestoId(presupuestoId);

        return presupuesto.getMontoInicial() + totalIngresos - totalGastos;
    }

    public List<PresupuestoResponseDTO> listarPorEstado(EstadoPresupuestoEnum estado) {
        return presupuestoRepository.findByEstado(estado).stream()
                .map(p -> PresupuestoMapper.toResponseDTO(p, calcularBalance(p.getId())))
                .toList();
    }

    public PresupuestoResponseDTO obtenerPorId(Long id) {
        Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Presupuesto no encontrado: " + id));

        return PresupuestoMapper.toResponseDTO(presupuesto, calcularBalance(id));
    }

    public PresupuestoResponseDTO crear(PresupuestoRequestDTO dto) {
        Presupuesto presupuesto = Presupuesto.builder()
                .nombre(dto.nombre())
                .montoInicial(dto.montoInicial())
                .frecuencia(dto.frecuencia())
                .montoRecurrente(dto.montoRecurrente())
                .estado(EstadoPresupuestoEnum.ACTIVO)
                .build();

        presupuesto = presupuestoRepository.save(presupuesto);
        return PresupuestoMapper.toResponseDTO(presupuesto, presupuesto.getMontoInicial());
    }

    public PresupuestoResponseDTO editar(Long id, PresupuestoRequestDTO dto) {
        Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Presupuesto no encontrado: " + id));

        presupuesto.setNombre(dto.nombre());
        presupuesto.setMontoInicial(dto.montoInicial());
        presupuesto.setFrecuencia(dto.frecuencia());
        presupuesto.setMontoRecurrente(dto.montoRecurrente());

        presupuesto = presupuestoRepository.save(presupuesto);
        return PresupuestoMapper.toResponseDTO(presupuesto, calcularBalance(id));
    }

    public PresupuestoResponseDTO archivar(Long id) {
        return cambiarEstado(id, EstadoPresupuestoEnum.ARCHIVADO);
    }

    public PresupuestoResponseDTO restaurar(Long id) {
        return cambiarEstado(id, EstadoPresupuestoEnum.ACTIVO);
    }

    private PresupuestoResponseDTO cambiarEstado(Long id, EstadoPresupuestoEnum nuevoEstado) {
        Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Presupuesto no encontrado: " + id));

        presupuesto.setEstado(nuevoEstado);
        presupuesto = presupuestoRepository.save(presupuesto);
        return PresupuestoMapper.toResponseDTO(presupuesto, calcularBalance(id));
    }

    public void eliminarDefinitivo(Long id) {
        Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Presupuesto no encontrado: " + id));

        if (presupuesto.getEstado() != EstadoPresupuestoEnum.ARCHIVADO) {
            throw new IllegalStateException("Solo se puede eliminar definitivamente un presupuesto archivado");
        }

        presupuestoRepository.delete(presupuesto);
    }
}