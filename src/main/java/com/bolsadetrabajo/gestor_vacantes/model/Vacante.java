package com.bolsadetrabajo.gestor_vacantes.model;

import com.bolsadetrabajo.gestor_vacantes.model.enums.EstadoVacante;
import com.bolsadetrabajo.gestor_vacantes.model.enums.TipoTrabajo;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "vacantes")
public class Vacante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(columnDefinition = "TEXT")
    private String requisitos;

    private String ubicacion;

    @Enumerated(EnumType.STRING)
    private TipoTrabajo tipoTrabajo;

    private BigDecimal salario;

    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion;

    @Enumerated(EnumType.STRING)
    private EstadoVacante estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleador_id", nullable = false)
    private Empleador empleador;
}