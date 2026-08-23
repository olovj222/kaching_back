package com.kaching.finanzas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kaching.finanzas.model.Persona;

import java.util.List;

public interface PersonaRepository extends JpaRepository<Persona, Long> {

    List<Persona> findByActivoTrue();
}
