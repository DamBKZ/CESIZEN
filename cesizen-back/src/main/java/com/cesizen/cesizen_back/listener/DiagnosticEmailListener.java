package com.cesizen.cesizen_back.listener;

import com.cesizen.cesizen_back.event.DiagnosticCompletedEvent;
import com.cesizen.cesizen_back.repository.UserRepository;
import com.cesizen.cesizen_back.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiagnosticEmailListener {

    private final EmailService emailService;
    private final UserRepository userRepository;

    @EventListener
    public void onDiagnosticCompleted(DiagnosticCompletedEvent event) {

        var survey = event.getSurvey();

        var user = userRepository.findById(survey.getUserId())
                .orElse(null);

        if (user == null) {
            log.warn("Impossible d'envoyer l'email de diagnostic : utilisateur {} introuvable", survey.getUserId());
            return;
        }

        emailService.sendDiagnosticResultEmail(
                user.getEmail(),
                survey.getScore(),
                survey.getRiskLevel(),
                survey.getCreatedAt().toString()
        );

        log.info("Email de diagnostic envoyé à {}", user.getEmail());
    }
}
