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

    // -------------------------------------------------------------------------
    // RESET PASSWORD
    // -------------------------------------------------------------------------
    @Override
    public void sendResetPasswordEmail(String to, String token) {
        String subject = "Réinitialisation de votre mot de passe CESIZen";
        String content =
                "Bonjour,\n\n" +
                "Vous avez demandé une réinitialisation de votre mot de passe.\n\n" +
                "Cliquez sur le lien ci-dessous pour définir un nouveau mot de passe :\n" +
                frontendUrl + "/reset-password?token=" + token + "\n\n" +
                "Ce lien est valable 30 minutes.\n\n" +
                "Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.\n\n" +
                "L'équipe CESIZen";

        sendEmail(to, subject, content);
        log.info("Email de réinitialisation envoyé à {}", to);
    }

    // -------------------------------------------------------------------------
    // DIAGNOSTIC RESULT
    // -------------------------------------------------------------------------
    @Override
    public void sendDiagnosticResultEmail(String to, int score, String riskLevel, String date) {
        String subject = "Résultats de votre diagnostic CESIZen";
        String content =
                "Bonjour,\n\n" +
                "Votre diagnostic CESIZen a été complété.\n\n" +
                "Voici vos résultats :\n" +
                "- Score : " + score + "\n" +
                "- Niveau de risque : " + riskLevel + "\n" +
                "- Date : " + date + "\n\n" +
                "Merci d'utiliser CESIZen.\n\n" +
                "L'équipe CESIZen";

        sendEmail(to, subject, content);
        log.info("Email de diagnostic envoyé à {}", to);
    }

    // -------------------------------------------------------------------------
    // ACCOUNT DELETION CONFIRMATION
    // -------------------------------------------------------------------------
    @Override
    public void sendAccountDeletionEmail(String email, String pseudo) {
        String subject = "Confirmation de suppression de votre compte";
        String content = """
                Bonjour %s,

                Votre compte a bien été supprimé de notre plateforme.

                Si vous n'êtes pas à l'origine de cette action, contactez immédiatement notre support.

                Cordialement,
                L'équipe CESIZen
                """.formatted(pseudo);

        sendEmail(email, subject, content);
        log.info("Email de suppression de compte envoyé à {}", email);
    }

    // -------------------------------------------------------------------------
    // MÉTHODE INTERNE GÉNÉRIQUE
    // -------------------------------------------------------------------------
    private void sendEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);

        } catch (Exception e) {
            log.error("Échec de l'envoi de l'email à {} : {}", to, e.getMessage());
            throw new IllegalStateException("Impossible d'envoyer l'email.");
        }
    }
}
