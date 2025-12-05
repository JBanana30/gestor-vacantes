package com.bolsadetrabajo.gestor_vacantes.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "aspirantes")
public class Aspirante extends Usuario {
    private String habilidades;
}