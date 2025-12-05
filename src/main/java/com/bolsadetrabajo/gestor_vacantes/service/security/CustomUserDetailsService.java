package com.bolsadetrabajo.gestor_vacantes.service.security;

import com.bolsadetrabajo.gestor_vacantes.model.Usuario;
import com.bolsadetrabajo.gestor_vacantes.repository.AspiranteRepository;
import com.bolsadetrabajo.gestor_vacantes.repository.EmpleadorRepository;
import org.springframework.security.authentication.DisabledException; // <-- ESTA LÍNEA FALTABA
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AspiranteRepository aspiranteRepo;
    private final EmpleadorRepository empleadorRepo;

    public CustomUserDetailsService(AspiranteRepository aspiranteRepo, EmpleadorRepository empleadorRepo) {
        this.aspiranteRepo = aspiranteRepo;
        this.empleadorRepo = empleadorRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        // Busca en aspirantes
        Optional<? extends Usuario> usuarioOpt = aspiranteRepo.findByCorreo(correo);

        // Si no está, busca en empleadores
        if (usuarioOpt.isEmpty()) {
            usuarioOpt = empleadorRepo.findByCorreo(correo);
        }

        Usuario usuario = usuarioOpt.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // VALIDACIÓN: Si no ha verificado su correo, no entra.
        if (!usuario.isActivo()) {
            throw new DisabledException("Cuenta no verificada. Revisa tu correo.");
        }

        return new User(usuario.getCorreo(), usuario.getContrasenaHash(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())));
    }
}