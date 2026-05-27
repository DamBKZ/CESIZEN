package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.dto.user.DiagnosticSubmitRequest;
import com.cesizen.cesizen_back.entity.DiagnosticAnswer;
import com.cesizen.cesizen_back.entity.DiagnosticEvent;
import com.cesizen.cesizen_back.entity.DiagnosticSurvey;
import com.cesizen.cesizen_back.event.DiagnosticCompletedEvent;
import com.cesizen.cesizen_back.repository.DiagnosticEventRepository;
import com.cesizen.cesizen_back.repository.DiagnosticSurveyRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DiagnosticServiceImplTest {

    private DiagnosticEventRepository eventRepository;
    private DiagnosticSurveyRepository surveyRepository;
    private ApplicationEventPublisher eventPublisher;

    private DiagnosticServiceImpl service;

    @BeforeEach
    void setup() {
        eventRepository = mock(DiagnosticEventRepository.class);
        surveyRepository = mock(DiagnosticSurveyRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);

        service = new DiagnosticServiceImpl(eventRepository, surveyRepository, eventPublisher);
    }

    @Test
    void submit_shouldCalculateScoreCreateSurveySaveAndPublishEvent() {

        DiagnosticEvent e1 = DiagnosticEvent.builder()
                .eventId("E1")
                .label("Event 1")
                .lcu(50)
                .build();

        DiagnosticEvent e2 = DiagnosticEvent.builder()
                .eventId("E2")
                .label("Event 2")
                .lcu(100)
                .build();

        when(eventRepository.findAll()).thenReturn(List.of(e1, e2));

        DiagnosticSubmitRequest request = new DiagnosticSubmitRequest();
        request.setUserId("USER123");
        request.setAnswers(Map.of(
                "E1", true,
                "E2", false
        ));

        DiagnosticSurvey savedSurvey = DiagnosticSurvey.builder()
                .surveyId("SURV123")
                .userId("USER123")
                .score(50)
                .riskLevel("LOW")
                .createdAt(LocalDateTime.now())
                .build();

        when(surveyRepository.save(any(DiagnosticSurvey.class))).thenReturn(savedSurvey);

        var response = service.submit(request);

        assertThat(response.getSurveyId()).isEqualTo("SURV123");
        assertThat(response.getScore()).isEqualTo(50);
        assertThat(response.getRiskLevel()).isEqualTo("LOW");
        assertThat(response.getCreatedAt()).isNotNull();

        ArgumentCaptor<DiagnosticSurvey> surveyCaptor = ArgumentCaptor.forClass(DiagnosticSurvey.class);
        verify(surveyRepository).save(surveyCaptor.capture());

        DiagnosticSurvey surveyBeforeSave = surveyCaptor.getValue();
        assertThat(surveyBeforeSave.getUserId()).isEqualTo("USER123");
        assertThat(surveyBeforeSave.getScore()).isEqualTo(50);
        assertThat(surveyBeforeSave.getRiskLevel()).isEqualTo("LOW");
        assertThat(surveyBeforeSave.getAnswers()).hasSize(2);

        DiagnosticAnswer a1 = surveyBeforeSave.getAnswers().get(0);
        DiagnosticAnswer a2 = surveyBeforeSave.getAnswers().get(1);

        assertThat(a1.getEvent()).isEqualTo(e1);
        assertThat(a1.isChecked()).isTrue();
        assertThat(a1.getSurvey()).isEqualTo(surveyBeforeSave);

        assertThat(a2.getEvent()).isEqualTo(e2);
        assertThat(a2.isChecked()).isFalse();
        assertThat(a2.getSurvey()).isEqualTo(surveyBeforeSave);

        ArgumentCaptor<DiagnosticCompletedEvent> eventCaptor = ArgumentCaptor.forClass(DiagnosticCompletedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        DiagnosticCompletedEvent publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent.getSurvey().getSurveyId()).isEqualTo("SURV123");
    }

    @Test
    void submit_shouldReturnMediumRisk() {

        DiagnosticEvent e1 = DiagnosticEvent.builder().eventId("E1").lcu(200).build();
        DiagnosticEvent e2 = DiagnosticEvent.builder().eventId("E2").lcu(50).build();

        when(eventRepository.findAll()).thenReturn(List.of(e1, e2));

        DiagnosticSubmitRequest request = new DiagnosticSubmitRequest();
        request.setUserId("USER123");
        request.setAnswers(Map.of("E1", true, "E2", true));

        DiagnosticSurvey savedSurvey = DiagnosticSurvey.builder()
                .surveyId("SURV999")
                .userId("USER123")
                .score(250)
                .riskLevel("MEDIUM")
                .createdAt(LocalDateTime.now())
                .build();

        when(surveyRepository.save(any(DiagnosticSurvey.class))).thenReturn(savedSurvey);

        var response = service.submit(request);

        assertThat(response.getRiskLevel()).isEqualTo("MEDIUM");
    }

    @Test
    void submit_shouldReturnHighRisk() {

        DiagnosticEvent e1 = DiagnosticEvent.builder().eventId("E1").lcu(200).build();
        DiagnosticEvent e2 = DiagnosticEvent.builder().eventId("E2").lcu(150).build();

        when(eventRepository.findAll()).thenReturn(List.of(e1, e2));

        DiagnosticSubmitRequest request = new DiagnosticSubmitRequest();
        request.setUserId("USER123");
        request.setAnswers(Map.of("E1", true, "E2", true));

        DiagnosticSurvey savedSurvey = DiagnosticSurvey.builder()
                .surveyId("SURV777")
                .userId("USER123")
                .score(350)
                .riskLevel("HIGH")
                .createdAt(LocalDateTime.now())
                .build();

        when(surveyRepository.save(any(DiagnosticSurvey.class))).thenReturn(savedSurvey);

        var response = service.submit(request);

        assertThat(response.getRiskLevel()).isEqualTo("HIGH");
    }

    @Test
    void history_shouldReturnMappedResponses() {

        DiagnosticSurvey s1 = DiagnosticSurvey.builder()
                .surveyId("S1")
                .userId("USER123")
                .score(100)
                .riskLevel("LOW")
                .createdAt(LocalDateTime.now())
                .build();

        DiagnosticSurvey s2 = DiagnosticSurvey.builder()
                .surveyId("S2")
                .userId("USER123")
                .score(300)
                .riskLevel("HIGH")
                .createdAt(LocalDateTime.now())
                .build();

        when(surveyRepository.findByUserIdOrderByCreatedAtDesc("USER123"))
                .thenReturn(List.of(s1, s2));

        var history = service.history("USER123");

        assertThat(history).hasSize(2);
        assertThat(history.get(0).getSurveyId()).isEqualTo("S1");
        assertThat(history.get(1).getSurveyId()).isEqualTo("S2");
    }
}
