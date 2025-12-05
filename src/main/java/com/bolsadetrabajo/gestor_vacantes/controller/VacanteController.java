package com.bolsadetrabajo.gestor_vacantes.controller;

import com.bolsadetrabajo.gestor_vacantes.model.Empleador;
import com.bolsadetrabajo.gestor_vacantes.model.Vacante;
import com.bolsadetrabajo.gestor_vacantes.model.enums.TipoTrabajo;
import com.bolsadetrabajo.gestor_vacantes.repository.EmpleadorRepository;
import com.bolsadetrabajo.gestor_vacantes.service.SolicitudService;
import com.bolsadetrabajo.gestor_vacantes.service.VacanteService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/vacantes")
public class VacanteController {

    private final VacanteService vacanteService;
    private final EmpleadorRepository empleadorRepository;
    private final SolicitudService solicitudService;

    public VacanteController(VacanteService vacanteService, EmpleadorRepository empleadorRepository, SolicitudService solicitudService) {
        this.vacanteService = vacanteService;
        this.empleadorRepository = empleadorRepository;
        this.solicitudService = solicitudService;
    }

    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        model.addAttribute("vacante", vacanteService.findById(id).orElseThrow());
        return "vacantes/detalle-vacante";
    }

    @GetMapping("/nueva")
    public String nuevaVacante(Model model) {
        model.addAttribute("vacante", new Vacante());
        model.addAttribute("tiposTrabajo", TipoTrabajo.values());
        return "vacantes/form-vacante";
    }

    @GetMapping("/editar/{id}")
    public String editarVacante(@PathVariable Long id, Model model) {
        model.addAttribute("vacante", vacanteService.findById(id).orElseThrow());
        model.addAttribute("tiposTrabajo", TipoTrabajo.values());
        return "vacantes/form-vacante";
    }

    @PostMapping("/guardar")
    public String guardarVacante(@ModelAttribute Vacante vacante, Authentication auth) {
        String correo = auth.getName();
        Empleador empleador = empleadorRepository.findByCorreo(correo).orElseThrow();

        if (vacante.getId() == null) {
            vacanteService.guardarVacante(vacante, empleador);
        } else {
            vacanteService.actualizarVacante(vacante, empleador);
        }
        return "redirect:/home"; // Redirige al panel unificado
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarVacante(@PathVariable Long id, Authentication auth) {
        String correo = auth.getName();
        Empleador empleador = empleadorRepository.findByCorreo(correo).orElseThrow();
        vacanteService.eliminarVacante(id, empleador);
        return "redirect:/home";
    }

    @GetMapping("/{id}/aspirantes")
    public String verAspirantes(@PathVariable Long id, Model model) {
        model.addAttribute("vacante", vacanteService.findById(id).orElseThrow());
        model.addAttribute("solicitudes", solicitudService.obtenerSolicitudesPorVacante(id));
        return "vacantes/lista-aspirantes";
    }
}