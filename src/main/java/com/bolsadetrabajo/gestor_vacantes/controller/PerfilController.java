package com.bolsadetrabajo.gestor_vacantes.controller;

import com.bolsadetrabajo.gestor_vacantes.repository.AspiranteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/perfil")
public class PerfilController {
    private final AspiranteRepository aspiranteRepo;

    public PerfilController(AspiranteRepository aspiranteRepo) { this.aspiranteRepo = aspiranteRepo; }

    @GetMapping("/mi-informacion")
    public String verPerfil(Model model, Authentication auth) {
        model.addAttribute("aspirante", aspiranteRepo.findByCorreo(auth.getName()).orElseThrow());
        return "perfil/mi-informacion";
    }
}