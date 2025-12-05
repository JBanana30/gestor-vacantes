package com.bolsadetrabajo.gestor_vacantes.service;

import com.bolsadetrabajo.gestor_vacantes.model.Aspirante;
import com.bolsadetrabajo.gestor_vacantes.model.Solicitud;
import com.bolsadetrabajo.gestor_vacantes.model.Vacante;
import com.bolsadetrabajo.gestor_vacantes.model.enums.EstadoSolicitud;
import com.bolsadetrabajo.gestor_vacantes.model.enums.EstadoVacante;
import com.bolsadetrabajo.gestor_vacantes.repository.SolicitudRepository;
import com.bolsadetrabajo.gestor_vacantes.repository.VacanteRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final EmailService emailService;
    private final VacanteRepository vacanteRepository;

    public SolicitudService(SolicitudRepository solicitudRepository, EmailService emailService, VacanteRepository vacanteRepository) {
        this.solicitudRepository = solicitudRepository;
        this.emailService = emailService;
        this.vacanteRepository = vacanteRepository;
    }

    public void crearSolicitud(Vacante vacante, Aspirante aspirante, String rutaCv) {
        if(vacante.getEstado() == EstadoVacante.CERRADA) {
            throw new IllegalStateException("Vacante cerrada.");
        }
        Solicitud nueva = new Solicitud();
        nueva.setAspirante(aspirante);
        nueva.setVacante(vacante);
        nueva.setFechaSolicitud(LocalDateTime.now());
        nueva.setEstado(EstadoSolicitud.PENDIENTE);
        nueva.setCvAdjunto(rutaCv);
        solicitudRepository.save(nueva);
    }

    public List<Solicitud> obtenerSolicitudesPorAspirante(Aspirante aspirante) {
        return solicitudRepository.findByAspiranteId(aspirante.getId());
    }

    public List<Solicitud> obtenerSolicitudesPorVacante(Long vacanteId) {
        return solicitudRepository.findByVacanteId(vacanteId);
    }

    public void aceptarSolicitud(Long solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId).orElseThrow();
        solicitud.setEstado(EstadoSolicitud.ACEPTADA);
        solicitudRepository.save(solicitud);

        emailService.notificarAspirante(solicitud.getAspirante().getCorreo(), solicitud.getVacante().getTitulo(), "ACEPTADA", solicitud.getId());

        // Cerrar vacante y rechazar a los demás
        Vacante vacante = solicitud.getVacante();
        vacante.setEstado(EstadoVacante.CERRADA);
        vacanteRepository.save(vacante);

        rechazarOtrasSolicitudes(vacante.getId(), solicitudId);
    }

    public void rechazarSolicitud(Long solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId).orElseThrow();
        solicitud.setEstado(EstadoSolicitud.RECHAZADA);
        solicitudRepository.save(solicitud);
        emailService.notificarAspirante(solicitud.getAspirante().getCorreo(), solicitud.getVacante().getTitulo(), "RECHAZADA", solicitud.getId());
    }

    public void cerrarVacanteYRechazarPendientes(Long vacanteId) {
        Vacante vacante = vacanteRepository.findById(vacanteId).orElseThrow();
        vacante.setEstado(EstadoVacante.CERRADA);
        vacanteRepository.save(vacante);
        rechazarOtrasSolicitudes(vacanteId, -1L);
    }

    private void rechazarOtrasSolicitudes(Long vacanteId, Long solicitudAceptadaId) {
        List<Solicitud> solicitudes = solicitudRepository.findByVacanteId(vacanteId);
        for (Solicitud s : solicitudes) {
            if (!s.getId().equals(solicitudAceptadaId) && s.getEstado() == EstadoSolicitud.PENDIENTE) {
                s.setEstado(EstadoSolicitud.RECHAZADA);
                solicitudRepository.save(s);
                emailService.notificarAspirante(s.getAspirante().getCorreo(), s.getVacante().getTitulo(), "RECHAZADA", s.getId());
            }
        }
    }
}