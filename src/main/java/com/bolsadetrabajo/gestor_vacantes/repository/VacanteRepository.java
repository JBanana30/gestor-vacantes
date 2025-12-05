package com.bolsadetrabajo.gestor_vacantes.repository;

import com.bolsadetrabajo.gestor_vacantes.model.Vacante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VacanteRepository extends JpaRepository<Vacante, Long> {
    List<Vacante> findByEmpleadorId(Long empleadorId);
}