package com.kaching.finanzas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kaching.finanzas.model.Gasto;


import java.util.List;

public interface GastoRepository extends JpaRepository<Gasto, Long> {

    List<Gasto> findByPresupuestoIdAndEliminadoFalseOrderByFechaGastoDesc(Long presupuestoId);

@Query("SELECT COALESCE(SUM(g.monto), 0) FROM Gasto g WHERE g.presupuesto.id = :presupuestoId AND g.eliminado = false")
Long sumMontoByPresupuestoId(@Param("presupuestoId") Long presupuestoId);
}