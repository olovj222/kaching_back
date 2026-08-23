package com.kaching.finanzas.model;

import java.time.LocalDateTime;

import com.kaching.finanzas.model.enums.EstadoPresupuestoEnum;
import com.kaching.finanzas.model.enums.FrecuenciaEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "presupuestos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Presupuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "monto_inicial", nullable = false, precision = 12, scale = 2)
    private Long montoInicial;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FrecuenciaEnum frecuencia;

    @Column(name = "monto_recurrente", precision = 12, scale = 2)
    private Long montoRecurrente;

    @Column(name = "fecha_creacion", nullable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoPresupuestoEnum estado = EstadoPresupuestoEnum.ACTIVO;

}
