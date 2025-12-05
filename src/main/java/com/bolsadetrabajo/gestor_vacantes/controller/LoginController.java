package com.bolsadetrabajo.gestor_vacantes.controller;

import com.bolsadetrabajo.gestor_vacantes.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final UsuarioService usuarioService;

    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/verificar")
    public String verificarCuenta(@RequestParam("id") Long id, Model model) {
        boolean verificado = usuarioService.verificarUsuario(id);
        if (verificado) {
            model.addAttribute("successMessage", "¡Cuenta verificada con éxito! Ahora puedes iniciar sesión.");
        } else {
            model.addAttribute("errorMessage", "Enlace de verificación inválido o usuario no encontrado.");
        }
        return "verificar-cuenta";
    }
}