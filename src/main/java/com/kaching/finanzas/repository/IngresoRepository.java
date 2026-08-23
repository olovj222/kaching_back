package com.kaching.finanzas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kaching.finanzas.model.Ingreso;


import java.util.List;

public interface IngresoRepository extends JpaRepository<Ingreso, Long> {

    List<Ingreso> findByPresupuestoIdAndEliminadoFalseOrderByFechaIngresoDesc(Long presupuestoId);

    @Query("SELECT COALESCE(SUM(i.monto), 0) FROM Ingreso i WHERE i.presupuesto.id = :presupuestoId AND i.eliminado = false")
    Long sumMontoByPresupuestoId(@Param("presupuestoId") Long presupuestoId);
}
