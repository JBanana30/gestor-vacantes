package com.bolsadetrabajo.gestor_vacantes.controller;

import com.bolsadetrabajo.gestor_vacantes.model.Empleador;
import com.bolsadetrabajo.gestor_vacantes.repository.EmpleadorRepository;
import com.bolsadetrabajo.gestor_vacantes.service.VacanteService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final VacanteService vacanteService;
    private final EmpleadorRepository empleadorRepository;

    public HomeController(VacanteService vacanteService, EmpleadorRepository empleadorRepository) {
        this.vacanteService = vacanteService;
        this.empleadorRepository = empleadorRepository;
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        // Si es empleador, cargamos sus vacantes para el panel
        if (hasRole("ROLE_EMPLEADOR", authentication)) {
            String correo = authentication.getName();
            Empleador empleador = empleadorRepository.findByCorreo(correo)
                    .orElseThrow(() -> new UsernameNotFoundException("Empleador no encontrado"));
            model.addAttribute("vacantes", vacanteService.obtenerVacantesPorEmpleador(empleador));
        }
        return "home";
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("vacantes", vacanteService.obtenerVacantesPublicadas());
        return "index";
    }

    private boolean hasRole(String role, Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role));
    }
}