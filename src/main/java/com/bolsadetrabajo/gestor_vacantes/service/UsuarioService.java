package com.bolsadetrabajo.gestor_vacantes.service;

import com.bolsadetrabajo.gestor_vacantes.model.Aspirante;
import com.bolsadetrabajo.gestor_vacantes.model.Empleador;
import com.bolsadetrabajo.gestor_vacantes.model.enums.Role;
import com.bolsadetrabajo.gestor_vacantes.repository.AspiranteRepository;
import com.bolsadetrabajo.gestor_vacantes.repository.EmpleadorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final AspiranteRepository aspiranteRepo;
    private final EmpleadorRepository empleadorRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UsuarioService(AspiranteRepository aspiranteRepo, EmpleadorRepository empleadorRepo, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.aspiranteRepo = aspiranteRepo;
        this.empleadorRepo = empleadorRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public void registrarAspirante(Aspirante aspirante) {
        validarCorreo(aspirante.getCorreo());
        aspirante.setContrasenaHash(passwordEncoder.encode(aspirante.getContrasenaHash()));
        aspirante.setRol(Role.ASPIRANTE);
        aspirante.setActivo(false); // Inactivo
        Aspirante guardado = aspiranteRepo.save(aspirante);
        emailService.enviarVerificacion(guardado.getCorreo(), guardado.getId());
    }

    public void registrarEmpleador(Empleador empleador) {
        validarCorreo(empleador.getCorreo());
        empleador.setContrasenaHash(passwordEncoder.encode(empleador.getContrasenaHash()));
        empleador.setRol(Role.EMPLEADOR);
        empleador.setActivo(false); // Inactivo
        Empleador guardado = empleadorRepo.save(empleador);
        emailService.enviarVerificacion(guardado.getCorreo(), guardado.getId());
    }

    public boolean verificarUsuario(Long id) {
        return aspiranteRepo.findById(id).map(u -> {
            u.setActivo(true);
            aspiranteRepo.save(u);
            return true;
        }).orElseGet(() -> empleadorRepo.findById(id).map(u -> {
            u.setActivo(true);
            empleadorRepo.save(u);
            return true;
        }).orElse(false));
    }

    private void validarCorreo(String correo) {
        if (aspiranteRepo.findByCorreo(correo).isPresent() || empleadorRepo.findByCorreo(correo).isPresent()) {
            throw new IllegalStateException("El correo ya está registrado.");
        }
    }
}