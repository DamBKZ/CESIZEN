package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.dto.user.DiagnosticHistoryResponse;
import com.cesizen.cesizen_back.dto.user.DiagnosticResponse;
import com.cesizen.cesizen_back.dto.user.DiagnosticSubmitRequest;
import com.cesizen.cesizen_back.entity.DiagnosticAnswer;
import com.cesizen.cesizen_back.entity.DiagnosticEvent;
import com.cesizen.cesizen_back.entity.DiagnosticSurvey;
import com.cesizen.cesizen_back.entity.User;
import com.cesizen.cesizen_back.event.DiagnosticCompletedEvent;
import com.cesizen.cesizen_back.repository.DiagnosticEventRepository;
import com.cesizen.cesizen_back.repository.DiagnosticSurveyRepository;
import com.cesizen.cesizen_back.service.DiagnosticService;
import org.springframework.security.access.AccessDeniedException;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DiagnosticServiceImpl implements DiagnosticService {

    private final DiagnosticEventRepository eventRepository;
    private final DiagnosticSurveyRepository surveyRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public DiagnosticResponse submit(
            DiagnosticSubmitRequest request,
            User currentUser
    ) {
        if (currentUser == null) {
throw new AccessDeniedException("Utilisateur non authentifié.");

        }

        Map<String, Boolean> submittedAnswers = request.getAnswers();

        if (submittedAnswers == null) {
            throw new IllegalArgumentException(
                    "Les réponses du diagnostic sont obligatoires."
            );
        }

        List<DiagnosticEvent> events = eventRepository.findAll();

        int score = 0;
        List<DiagnosticAnswer> answers = new ArrayList<>();

        DiagnosticSurvey survey = DiagnosticSurvey.builder()
                .userId(currentUser.getUserId())
                .score(0)
                .riskLevel("UNKNOWN")
                .build();

        for (DiagnosticEvent event : events) {
            boolean checked = submittedAnswers.getOrDefault(
                    event.getEventId(),
                    false
            );

            if (checked) {
                score += event.getLcu();
            }

            DiagnosticAnswer answer = DiagnosticAnswer.builder()
                    .survey(survey)
                    .event(event)
                    .checked(checked)
                    .build();

            answers.add(answer);
        }

        survey.setScore(score);
        survey.setRiskLevel(calculateRisk(score));
        survey.setAnswers(answers);

        /*
         * Force l'insertion du diagnostic et l'initialisation de createdAt
         * avant de publier l'événement.
         */
        survey = surveyRepository.saveAndFlush(survey);

        eventPublisher.publishEvent(
                new DiagnosticCompletedEvent(this, survey)
        );

        return DiagnosticResponse.builder()
                .surveyId(survey.getSurveyId())
                .score(survey.getScore())
                .riskLevel(survey.getRiskLevel())
                .createdAt(survey.getCreatedAt().toString())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiagnosticHistoryResponse> history(User currentUser) {
        if (currentUser == null) {
throw new AccessDeniedException("Utilisateur non authentifié.");

        }

        return surveyRepository
                .findByUserIdOrderByCreatedAtDesc(
                        currentUser.getUserId()
                )
                .stream()
                .map(survey -> DiagnosticHistoryResponse.builder()
                        .surveyId(survey.getSurveyId())
                        .score(survey.getScore())
                        .riskLevel(survey.getRiskLevel())
                        .createdAt(
                                survey.getCreatedAt() != null
                                        ? survey.getCreatedAt().toString()
                                        : null
                        )
                        .build()
                )
                .toList();
    }

    private String calculateRisk(int score) {
        if (score < 150) {
            return "LOW";
        }

        if (score < 300) {
            return "MEDIUM";
        }

        return "HIGH";
    }
}
