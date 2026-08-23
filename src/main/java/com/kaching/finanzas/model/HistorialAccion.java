package com.kaching.finanzas.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.kaching.finanzas.model.enums.TipoAccionEnum;
import com.kaching.finanzas.model.enums.TipoEntidadEnum;

@Entity
@Table(name = "historial_acciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class HistorialAccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presupuesto_id", nullable = false)
    private Presupuesto presupuesto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_entidad", nullable = false)
    private TipoEntidadEnum tipoEntidad;

    @Column(name = "entidad_id", nullable = false)
    private Long entidadId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_accion", nullable = false)
    private TipoAccionEnum tipoAccion;

    @Lob
    @Column(name = "datos_antes", columnDefinition = "TEXT")
    private String datosAntes;

    @Lob
    @Column(name = "datos_despues", columnDefinition = "TEXT")
    private String datosDespues;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    @Column(name = "fecha_accion", nullable = false)
    @Builder.Default
    private LocalDateTime fechaAccion = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private Boolean deshecho = false;
}
