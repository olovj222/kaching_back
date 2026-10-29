package com.kaching.finanzas.service;

import com.kaching.finanzas.dto.UndoRedoResponseDTO;
import com.kaching.finanzas.dto.snapshots.GastoSnapshotDTO;
import com.kaching.finanzas.dto.snapshots.IngresoSnapshotDTO;
import com.kaching.finanzas.model.Gasto;
import com.kaching.finanzas.model.HistorialAccion;
import com.kaching.finanzas.model.Ingreso;
import com.kaching.finanzas.model.Persona;
import com.kaching.finanzas.model.Presupuesto;
import com.kaching.finanzas.model.enums.TipoAccionEnum;
import com.kaching.finanzas.model.enums.TipoEntidadEnum;
import com.kaching.finanzas.repository.GastoRepository;
import com.kaching.finanzas.repository.HistorialAccionRepository;
import com.kaching.finanzas.repository.IngresoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class HistorialService {

    private static final String MSG_CONFLICTO =
            "Otra persona modificó este registro después de tu acción, "
                    + "así que no se puede deshacer ni rehacer";

    private final HistorialAccionRepository historialRepository;
    private final GastoRepository gastoRepository;
    private final IngresoRepository ingresoRepository;
    private final PresupuestoService presupuestoService;
    private final JsonMapper jsonMapper; // Jackson 3 (Spring Boot 4)

    // Se llama desde GastoService / IngresoService, dentro de SU transaccion:
    // el cambio real y su registro en el historial se guardan juntos o no se guarda nada.
    @Transactional
    public void registrar(Presupuesto presupuesto, Persona persona,
                          TipoEntidadEnum tipoEntidad, Long entidadId,
                          TipoAccionEnum tipoAccion, Object antes, Object despues) {

        // Una accion nueva invalida lo que esta persona tenia pendiente de rehacer.
        historialRepository.descartarRehacer(presupuesto.getId(), persona.getId());

        historialRepository.save(HistorialAccion.builder()
                .presupuesto(presupuesto)
                .persona(persona)
                .tipoEntidad(tipoEntidad)
                .entidadId(entidadId)
                .tipoAccion(tipoAccion)
                .datosAntes(jsonMapper.writeValueAsString(antes))
                .datosDespues(jsonMapper.writeValueAsString(despues))
                .build());
    }

    @Transactional
    public UndoRedoResponseDTO deshacer(Long presupuestoId, Long personaId) {
        HistorialAccion accion = historialRepository
                .findFirstByPresupuestoIdAndPersonaIdAndDeshechoFalseOrderByIdDesc(presupuestoId, personaId)
                .orElseThrow(() -> new IllegalStateException("No hay acciones tuyas para deshacer en esta hoja"));

        return ejecutar(accion, true);
    }

    @Transactional
    public UndoRedoResponseDTO rehacer(Long presupuestoId, Long personaId) {
        HistorialAccion accion = historialRepository
                .findFirstByPresupuestoIdAndPersonaIdAndDeshechoTrueOrderByIdAsc(presupuestoId, personaId)
                .orElseThrow(() -> new IllegalStateException("No hay acciones tuyas para rehacer en esta hoja"));

        return ejecutar(accion, false);
    }

    private UndoRedoResponseDTO ejecutar(HistorialAccion accion, boolean deshacer) {
        // Deshacer: el registro debe estar como quedo DESPUES de la accion y vuelve al ANTES.
        // Rehacer: el registro debe estar como estaba ANTES y vuelve al DESPUES.
        String esperadoJson = deshacer ? accion.getDatosDespues() : accion.getDatosAntes();
        String destinoJson = deshacer ? accion.getDatosAntes() : accion.getDatosDespues();

        String resultado = switch (accion.getTipoEntidad()) {
            case GASTO -> aplicarGasto(accion.getEntidadId(),
                    jsonMapper.readValue(esperadoJson, GastoSnapshotDTO.class),
                    jsonMapper.readValue(destinoJson, GastoSnapshotDTO.class));
            case INGRESO -> aplicarIngreso(accion.getEntidadId(),
                    jsonMapper.readValue(esperadoJson, IngresoSnapshotDTO.class),
                    jsonMapper.readValue(destinoJson, IngresoSnapshotDTO.class));
        };

        accion.setDeshecho(deshacer);

        Long presupuestoId = accion.getPresupuesto().getId();
        return new UndoRedoResponseDTO(
                accion.getTipoEntidad(),
                accion.getEntidadId(),
                accion.getTipoAccion(),
                resultado,
                presupuestoService.obtenerPorId(presupuestoId)
        );
    }

    private String aplicarGasto(Long id, GastoSnapshotDTO esperado, GastoSnapshotDTO destino) {
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gasto no encontrado: " + id));

        boolean coincide = Objects.equals(gasto.getMonto(), esperado.monto())
                && Objects.equals(gasto.getDescripcion(), esperado.descripcion())
                && Objects.equals(gasto.getEliminado(), esperado.eliminado());
        if (!coincide) {
            throw new IllegalStateException(MSG_CONFLICTO);
        }

        gasto.setMonto(destino.monto());
        gasto.setDescripcion(destino.descripcion());
        gasto.setEliminado(destino.eliminado());
        return describirEfecto(esperado.eliminado(), destino.eliminado());
    }

    private String aplicarIngreso(Long id, IngresoSnapshotDTO esperado, IngresoSnapshotDTO destino) {
        Ingreso ingreso = ingresoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingreso no encontrado: " + id));

        boolean coincide = Objects.equals(ingreso.getMonto(), esperado.monto())
                && Objects.equals(ingreso.getDescripcion(), esperado.descripcion())
                && Objects.equals(ingreso.getEliminado(), esperado.eliminado());
        if (!coincide) {
            throw new IllegalStateException(MSG_CONFLICTO);
        }

        ingreso.setMonto(destino.monto());
        ingreso.setDescripcion(destino.descripcion());
        ingreso.setEliminado(destino.eliminado());
        return describirEfecto(esperado.eliminado(), destino.eliminado());
    }

    // Efecto neto visible para el usuario, para que el frontend sepa como actualizar la lista.
    private String describirEfecto(boolean eliminadoAntes, boolean eliminadoDespues) {
        if (!eliminadoAntes && eliminadoDespues) return "ELIMINADO";
        if (eliminadoAntes && !eliminadoDespues) return "RESTAURADO";
        return "EDITADO";
    }
}
