package com.bolsadetrabajo.gestor_vacantes.dto;

import lombok.Data;

@Data
public class RegistroAspiranteDTO {
    private String nombre;
    private String correo;
    private String contrasena;
    private String habilidades;
}