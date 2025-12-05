package com.bolsadetrabajo.gestor_vacantes.repository;

import com.bolsadetrabajo.gestor_vacantes.model.Empleador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmpleadorRepository extends JpaRepository<Empleador, Long> {
    Optional<Empleador> findByCorreo(String correo);
}