package com.kaching.finanzas.repository;

import com.kaching.finanzas.model.HistorialAccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HistorialAccionRepository extends JpaRepository<HistorialAccion, Long> {

    // UNDO: la accion mas reciente de esta persona en esta hoja que aun no se deshizo.
    Optional<HistorialAccion> findFirstByPresupuestoIdAndPersonaIdAndDeshechoFalseOrderByIdDesc(
            Long presupuestoId, Long personaId);

    // REDO: el undo va de la mas nueva a la mas vieja, asi que lo ultimo que se
    // deshizo es la MAS ANTIGUA de las acciones deshechas.
    Optional<HistorialAccion> findFirstByPresupuestoIdAndPersonaIdAndDeshechoTrueOrderByIdAsc(
            Long presupuestoId, Long personaId);

    // Al hacer una accion nueva se corta la rama de redo (como en un editor de texto).
    @Modifying
    @Query("""
        DELETE FROM HistorialAccion h
        WHERE h.presupuesto.id = :presupuestoId
          AND h.persona.id = :personaId
          AND h.deshecho = true
        """)
    void descartarRehacer(@Param("presupuestoId") Long presupuestoId,
                          @Param("personaId") Long personaId);
}