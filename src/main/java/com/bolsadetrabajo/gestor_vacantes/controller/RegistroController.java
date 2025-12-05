package com.bolsadetrabajo.gestor_vacantes.controller;

import com.bolsadetrabajo.gestor_vacantes.dto.RegistroAspiranteDTO;
import com.bolsadetrabajo.gestor_vacantes.dto.RegistroEmpleadorDTO;
import com.bolsadetrabajo.gestor_vacantes.model.Aspirante;
import com.bolsadetrabajo.gestor_vacantes.model.Empleador;
import com.bolsadetrabajo.gestor_vacantes.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/registro")
public class RegistroController {

    private final UsuarioService usuarioService;

    public RegistroController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("aspirante", new RegistroAspiranteDTO());
        model.addAttribute("empleador", new RegistroEmpleadorDTO());
        return "registro";
    }

    @PostMapping("/aspirante")
    public String registrarAspirante(@ModelAttribute("aspirante") RegistroAspiranteDTO dto, RedirectAttributes redirectAttributes) {
        try {
            Aspirante aspirante = new Aspirante();
            aspirante.setNombre(dto.getNombre());
            aspirante.setCorreo(dto.getCorreo());
            aspirante.setContrasenaHash(dto.getContrasena());
            aspirante.setHabilidades(dto.getHabilidades());

            usuarioService.registrarAspirante(aspirante);
            redirectAttributes.addFlashAttribute("successMessage", "Registro exitoso. ¡Revisa tu correo para activar tu cuenta!");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/registro";
        }
    }

    @PostMapping("/empleador")
    public String registrarEmpleador(@ModelAttribute("empleador") RegistroEmpleadorDTO dto, RedirectAttributes redirectAttributes) {
        try {
            Empleador empleador = new Empleador();
            empleador.setNombre(dto.getNombre());
            empleador.setCorreo(dto.getCorreo());
            empleador.setContrasenaHash(dto.getContrasena());
            empleador.setEmpresa(dto.getEmpresa());

            usuarioService.registrarEmpleador(empleador);
            redirectAttributes.addFlashAttribute("successMessage", "Registro exitoso. ¡Revisa tu correo para activar tu cuenta!");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/registro";
        }
    }
}