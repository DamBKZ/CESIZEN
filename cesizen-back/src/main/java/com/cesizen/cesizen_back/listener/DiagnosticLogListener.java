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
    public void onDiagnosticCompleted(DiagnosticCompletedEvent event) {

        var survey = event.getSurvey();

        String content = "Diagnostic completed with score " + survey.getScore()
                + " and risk level " + survey.getRiskLevel();

        logService.createLog(survey.getUserId(), content);

        log.info("Log created for diagnostic {}", survey.getSurveyId());
    }
}
