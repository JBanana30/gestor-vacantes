package com.bolsadetrabajo.gestor_vacantes.controller;

import com.bolsadetrabajo.gestor_vacantes.model.Aspirante;
import com.bolsadetrabajo.gestor_vacantes.model.Solicitud;
import com.bolsadetrabajo.gestor_vacantes.model.Vacante;
import com.bolsadetrabajo.gestor_vacantes.model.enums.EstadoSolicitud;
import com.bolsadetrabajo.gestor_vacantes.repository.AspiranteRepository;
import com.bolsadetrabajo.gestor_vacantes.repository.SolicitudRepository;
import com.bolsadetrabajo.gestor_vacantes.service.SolicitudService;
import com.bolsadetrabajo.gestor_vacantes.service.StorageService;
import com.bolsadetrabajo.gestor_vacantes.service.VacanteService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final VacanteService vacanteService;
    private final AspiranteRepository aspiranteRepository;
    private final StorageService storageService;
    private final SolicitudRepository solicitudRepository;

    public SolicitudController(SolicitudService solicitudService, VacanteService vacanteService, AspiranteRepository aspiranteRepository, StorageService storageService, SolicitudRepository solicitudRepository) {
        this.solicitudService = solicitudService;
        this.vacanteService = vacanteService;
        this.aspiranteRepository = aspiranteRepository;
        this.storageService = storageService;
        this.solicitudRepository = solicitudRepository;
    }

    @GetMapping("/postular/{vacanteId}")
    public String formPostulacion(@PathVariable Long vacanteId, Model model) {
        model.addAttribute("vacante", vacanteService.findById(vacanteId).orElseThrow());
        return "solicitudes/form-solicitud";
    }

    @PostMapping("/enviar")
    public String enviarPostulacion(@RequestParam Long vacanteId, @RequestParam MultipartFile cvFile, Authentication auth, RedirectAttributes redir) {
        try {
            String filename = storageService.store(cvFile);
            Aspirante aspirante = aspiranteRepository.findByCorreo(auth.getName()).orElseThrow();
            Vacante vacante = vacanteService.findById(vacanteId).orElseThrow();

            solicitudService.crearSolicitud(vacante, aspirante, filename);
            redir.addFlashAttribute("successMessage", "¡Postulación enviada!");
        } catch (Exception e) {
            redir.addFlashAttribute("errorMessage", "Error al postularse: " + e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/mis-postulaciones")
    public String misPostulaciones(Model model, Authentication auth) {
        Aspirante aspirante = aspiranteRepository.findByCorreo(auth.getName()).orElseThrow();
        model.addAttribute("solicitudes", solicitudService.obtenerSolicitudesPorAspirante(aspirante));
        return "solicitudes/mis-postulaciones";
    }

    @PostMapping("/aceptar/{id}")
    public String aceptar(@PathVariable Long id) {
        solicitudService.aceptarSolicitud(id);
        Long vacanteId = solicitudRepository.findById(id).orElseThrow().getVacante().getId();
        return "redirect:/vacantes/" + vacanteId + "/aspirantes";
    }

    @PostMapping("/rechazar/{id}")
    public String rechazar(@PathVariable Long id) {
        solicitudService.rechazarSolicitud(id);
        Long vacanteId = solicitudRepository.findById(id).orElseThrow().getVacante().getId();
        return "redirect:/vacantes/" + vacanteId + "/aspirantes";
    }

    @PostMapping("/vacante/cerrar/{id}")
    public String cerrarVacante(@PathVariable Long id, RedirectAttributes redir) {
        solicitudService.cerrarVacanteYRechazarPendientes(id);
        redir.addFlashAttribute("successMessage", "Vacante cerrada y pendientes rechazados.");
        return "redirect:/home";
    }

    @GetMapping("/confirmar/{id}")
    public String confirmar(@PathVariable Long id, Authentication auth, RedirectAttributes redir) {
        Solicitud sol = solicitudRepository.findById(id).orElseThrow();
        if(sol.getAspirante().getCorreo().equals(auth.getName()) && sol.getEstado() == EstadoSolicitud.ACEPTADA) {
            sol.setEstado(EstadoSolicitud.CONFIRMADA);
            solicitudRepository.save(sol);
            redir.addFlashAttribute("successMessage", "¡Asistencia confirmada!");
        }
        return "redirect:/solicitudes/mis-postulaciones";
    }
}