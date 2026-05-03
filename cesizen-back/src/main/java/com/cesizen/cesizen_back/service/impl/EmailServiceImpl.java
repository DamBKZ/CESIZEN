package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Override
    public void sendResetPasswordEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject("Réinitialisation de votre mot de passe CESIZen");
            message.setText(
                    "Bonjour,\n\n" +
                    "Vous avez demandé une réinitialisation de votre mot de passe.\n\n" +
                    "Cliquez sur le lien ci-dessous pour définir un nouveau mot de passe :\n" +
                    frontendUrl + "/reset-password?token=" + token + "\n\n" +
                    "Ce lien est valable 30 minutes.\n\n" +
                    "Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.\n\n" +
                    "L'équipe CESIZen"
            );

            mailSender.send(message);
            log.info("Email de réinitialisation envoyé à {}", to);

        } catch (Exception e) {
            log.error("Échec de l'envoi de l'email de réinitialisation à {} : {}", to, e.getMessage());
            throw new IllegalStateException("Impossible d'envoyer l'email de réinitialisation.");
        }
    }
}