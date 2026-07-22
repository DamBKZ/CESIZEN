package com.cesizen.cesizen_back.listener;

import com.cesizen.cesizen_back.event.DiagnosticCompletedEvent;
import com.cesizen.cesizen_back.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiagnosticLogListener {

    private final LogService logService;

    @EventListener
    public void onDiagnosticCompleted(
            DiagnosticCompletedEvent event
    ) {
        var survey = event.getSurvey();

        try {
            String content =
                    "Diagnostic terminé avec le score "
                            + survey.getScore()
                            + " et le niveau de risque "
                            + survey.getRiskLevel();

            logService.createLog(
                    survey.getUserId(),
                    content
            );

            log.info(
                    "Log créé pour le diagnostic {}",
                    survey.getSurveyId()
            );

        } catch (Exception exception) {
            /*
             * Une erreur de journalisation ne doit pas annuler
             * l'enregistrement du diagnostic.
             */
            log.error(
                    "Diagnostic {} enregistré, mais le log n'a pas pu être créé",
                    survey.getSurveyId(),
                    exception
            );
        }
    }
}
