package com.cesizen.cesizen_back.event;

import com.cesizen.cesizen_back.entity.DiagnosticSurvey;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DiagnosticCompletedEvent extends ApplicationEvent {

    private final DiagnosticSurvey survey;

    public DiagnosticCompletedEvent(Object source, DiagnosticSurvey survey) {
        super(source);
        this.survey = survey;
    }
}
