package com.bolsadetrabajo.gestor_vacantes.repository;

import com.bolsadetrabajo.gestor_vacantes.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findByAspiranteId(Long aspiranteId);
    List<Solicitud> findByVacanteId(Long vacanteId);
}