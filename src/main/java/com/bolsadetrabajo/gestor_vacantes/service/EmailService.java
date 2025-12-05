package com.bolsadetrabajo.gestor_vacantes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    // Correo de Bienvenida / Verificación
    public void enviarVerificacion(String correoDestino, Long usuarioId) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(remitente);
            message.setTo(correoDestino);
            message.setSubject("Verifica tu cuenta - Bolsa de Trabajo");
            message.setText("¡Bienvenido!\n\n" +
                    "Para activar tu cuenta, por favor haz clic en el siguiente enlace:\n" +
                    "http://localhost:8080/verificar?id=" + usuarioId + "\n\n" +
                    "Si no te has registrado, ignora este mensaje.");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error enviando verificación: " + e.getMessage());
        }
    }

    // Correo de Estado de Solicitud (Aceptado/Rechazado)
    public void notificarAspirante(String correoDestino, String nombreVacante, String estado, Long solicitudId) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(remitente);
            message.setTo(correoDestino);
            message.setSubject("Actualización: " + nombreVacante);

            String texto;
            if ("ACEPTADA".equalsIgnoreCase(estado)) {
                texto = "¡Felicidades!\n\nHas sido ACEPTADO para la vacante '" + nombreVacante + "'.\n\n" +
                        "Por favor confirma tu interés aquí: http://localhost:8080/solicitudes/confirmar/" + solicitudId + "\n\n" +
                        "El empleador te contactará pronto.";
            } else {
                texto = "Hola.\n\nTe informamos que tu solicitud para la vacante '" + nombreVacante + "' ha sido RECHAZADA.\n" +
                        "Te invitamos a seguir buscando en nuestra plataforma.";
            }

            message.setText(texto);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error enviando notificación: " + e.getMessage());
        }
    }
}