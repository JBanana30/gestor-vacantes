package com.bolsadetrabajo.gestor_vacantes.service;

import com.bolsadetrabajo.gestor_vacantes.model.Empleador;
import com.bolsadetrabajo.gestor_vacantes.model.Vacante;
import com.bolsadetrabajo.gestor_vacantes.model.enums.EstadoVacante;
import com.bolsadetrabajo.gestor_vacantes.repository.VacanteRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VacanteService {

    private final VacanteRepository vacanteRepository;

    public VacanteService(VacanteRepository vacanteRepository) {
        this.vacanteRepository = vacanteRepository;
    }

    public List<Vacante> obtenerVacantesPublicadas() {
        return vacanteRepository.findAll().stream()
                .filter(v -> v.getEstado() == EstadoVacante.PUBLICADA)
                .toList();
    }

    public List<Vacante> obtenerVacantesPorEmpleador(Empleador empleador) {
        return vacanteRepository.findByEmpleadorId(empleador.getId());
    }

    public Optional<Vacante> findById(Long id) {
        return vacanteRepository.findById(id);
    }

    public void guardarVacante(Vacante vacante, Empleador empleador) {
        vacante.setEmpleador(empleador);
        vacante.setFechaPublicacion(LocalDateTime.now());
        vacante.setEstado(EstadoVacante.PUBLICADA);
        vacanteRepository.save(vacante);
    }

    public void actualizarVacante(Vacante vacanteActualizada, Empleador empleador) {
        Vacante vacante = findById(vacanteActualizada.getId()).orElseThrow();
        if (!vacante.getEmpleador().getId().equals(empleador.getId())) {
            throw new SecurityException("No tienes permiso");
        }
        vacante.setTitulo(vacanteActualizada.getTitulo());
        vacante.setDescripcion(vacanteActualizada.getDescripcion());
        vacante.setRequisitos(vacanteActualizada.getRequisitos());
        vacante.setUbicacion(vacanteActualizada.getUbicacion());
        vacante.setSalario(vacanteActualizada.getSalario());
        vacante.setTipoTrabajo(vacanteActualizada.getTipoTrabajo());
        vacanteRepository.save(vacante);
    }

    public void eliminarVacante(Long id, Empleador empleador) {
        Vacante vacante = findById(id).orElseThrow();
        if (!vacante.getEmpleador().getId().equals(empleador.getId())) {
            throw new SecurityException("No tienes permiso");
        }
        vacanteRepository.deleteById(id);
    }
}