package com.bolsadetrabajo.gestor_vacantes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "empleadores")
public class Empleador extends Usuario {
    @Column(nullable = false)
    private String empresa;
}