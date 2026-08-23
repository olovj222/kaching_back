package com.kaching.finanzas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kaching.finanzas.model.HistorialAccion;

import java.util.Optional;

public interface HistorialAccionRepository extends JpaRepository<HistorialAccion, Long> {

    // Última acción NO deshecha de esta persona en esta hoja -> candidata a UNDO
    @Query("""
        SELECT h FROM HistorialAccion h
        WHERE h.presupuesto.id = :presupuestoId
          AND h.persona.id = :personaId
          AND h.deshecho = false
        ORDER BY h.fechaAccion DESC
        """)
    Optional<HistorialAccion> findUltimaAccionParaDeshacer(
            @Param("presupuestoId") Long presupuestoId,
            @Param("personaId") Long personaId
    );

    // Última acción deshecha de esta persona en esta hoja -> candidata a REDO
    @Query("""
        SELECT h FROM HistorialAccion h
        WHERE h.presupuesto.id = :presupuestoId
          AND h.persona.id = :personaId
          AND h.deshecho = true
        ORDER BY h.fechaAccion DESC
        """)
    Optional<HistorialAccion> findUltimaAccionParaRehacer(
            @Param("presupuestoId") Long presupuestoId,
            @Param("personaId") Long personaId
    );
}