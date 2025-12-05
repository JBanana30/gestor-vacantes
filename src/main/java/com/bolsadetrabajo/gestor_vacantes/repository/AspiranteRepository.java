package com.bolsadetrabajo.gestor_vacantes.repository;

import com.bolsadetrabajo.gestor_vacantes.model.Aspirante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AspiranteRepository extends JpaRepository<Aspirante, Long> {
    Optional<Aspirante> findByCorreo(String correo);
}