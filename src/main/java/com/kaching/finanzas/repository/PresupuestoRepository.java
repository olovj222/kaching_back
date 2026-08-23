package com.kaching.finanzas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kaching.finanzas.model.Presupuesto;
import com.kaching.finanzas.model.enums.EstadoPresupuestoEnum;

import java.util.List;

public interface PresupuestoRepository extends JpaRepository<Presupuesto, Long> {

    List<Presupuesto> findByEstado(EstadoPresupuestoEnum estado);
}
