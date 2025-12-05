package com.bolsadetrabajo.gestor_vacantes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationSuccessHandler customHandler;

    // Inyectamos nuestro handler personalizado
    public SecurityConfig(AuthenticationSuccessHandler customHandler) {
        this.customHandler = customHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitamos CSRF para facilitar las pruebas (en producción se debería evaluar)
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // --- RUTAS PÚBLICAS ---
                        // Archivos estáticos, login, registro y verificación de cuenta
                        .requestMatchers("/", "/registro/**", "/login", "/css/**", "/js/**", "/verificar/**").permitAll()
                        // Permitimos ver el detalle de una vacante a cualquiera (GET)
                        .requestMatchers(HttpMethod.GET, "/vacantes/{id:\\d+}").permitAll()

                        // --- RUTAS DE ASPIRANTE ---
                        .requestMatchers("/solicitudes/**", "/perfil/**").hasRole("ASPIRANTE")

                        // --- RUTAS DE EMPLEADOR ---
                        // El panel, la gestión de vacantes y ver los CVs subidos
                        .requestMatchers("/home", "/vacantes/**", "/uploads/**").hasRole("EMPLEADOR")

                        // --- CUALQUIER OTRA RUTA REQUIERE LOGIN ---
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        // Usamos nuestro handler para redirigir según el rol
                        .successHandler(customHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}