package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.dto.user.DiagnosticSubmitRequest;
import com.cesizen.cesizen_back.dto.user.DiagnosticResponse;
import com.cesizen.cesizen_back.entity.DiagnosticAnswer;
import com.cesizen.cesizen_back.entity.DiagnosticEvent;
import com.cesizen.cesizen_back.entity.DiagnosticSurvey;
import com.cesizen.cesizen_back.repository.DiagnosticEventRepository;
import com.cesizen.cesizen_back.repository.DiagnosticSurveyRepository;
import com.cesizen.cesizen_back.service.impl.DiagnosticServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DiagnosticServiceImplTest {

    private DiagnosticEventRepository eventRepository;
    private DiagnosticSurveyRepository surveyRepository;
    private DiagnosticServiceImpl service;

    @BeforeEach
    void setup() {
        eventRepository = mock(DiagnosticEventRepository.class);
        surveyRepository = mock(DiagnosticSurveyRepository.class);
        service = new DiagnosticServiceImpl(eventRepository, surveyRepository);
    }

    @Test
    void submit_shouldCalculateScoreAndRiskLevel() {
        // GIVEN
        DiagnosticEvent e1 = new DiagnosticEvent(UUID.randomUUID().toString(), "Event 1", 50);
        DiagnosticEvent e2 = new DiagnosticEvent(UUID.randomUUID().toString(), "Event 2", 30);

        when(eventRepository.findAll()).thenReturn(List.of(e1, e2));

        Map<String, Boolean> answers = new HashMap<>();
        answers.put(e1.getEventId(), true);
        answers.put(e2.getEventId(), false);

        DiagnosticSubmitRequest request = new DiagnosticSubmitRequest();
        request.setUserId("user-123");
        request.setAnswers(answers);

        // WHEN
        DiagnosticResponse response = service.submit(request);

        // THEN
        assertThat(response.getScore()).isEqualTo(50);
        assertThat(response.getRiskLevel()).isEqualTo("LOW");

        ArgumentCaptor<DiagnosticSurvey> captor = ArgumentCaptor.forClass(DiagnosticSurvey.class);
        verify(surveyRepository).save(captor.capture());

        DiagnosticSurvey saved = captor.getValue();
        assertThat(saved.getScore()).isEqualTo(50);
        assertThat(saved.getRiskLevel()).isEqualTo("LOW");
        assertThat(saved.getAnswers()).hasSize(2);
    }

    @Test
    void submit_shouldReturnHighRiskWhenScoreAbove300() {
        // GIVEN
        DiagnosticEvent e1 = new DiagnosticEvent(UUID.randomUUID().toString(), "Event 1", 200);
        DiagnosticEvent e2 = new DiagnosticEvent(UUID.randomUUID().toString(), "Event 2", 150);

        when(eventRepository.findAll()).thenReturn(List.of(e1, e2));

        Map<String, Boolean> answers = Map.of(
                e1.getEventId(), true,
                e2.getEventId(), true
        );

        DiagnosticSubmitRequest request = new DiagnosticSubmitRequest();
        request.setUserId("user-123");
        request.setAnswers(answers);

        // WHEN
        DiagnosticResponse response = service.submit(request);

        // THEN
        assertThat(response.getRiskLevel()).isEqualTo("HIGH");
    }
}
