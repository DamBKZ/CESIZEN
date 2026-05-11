package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.dto.user.DiagnosticSubmitRequest;
import com.cesizen.cesizen_back.dto.user.DiagnosticHistoryResponse;
import com.cesizen.cesizen_back.dto.user.DiagnosticResponse;
import com.cesizen.cesizen_back.entity.DiagnosticAnswer;
import com.cesizen.cesizen_back.entity.DiagnosticEvent;
import com.cesizen.cesizen_back.entity.DiagnosticSurvey;
import com.cesizen.cesizen_back.repository.DiagnosticEventRepository;
import com.cesizen.cesizen_back.repository.DiagnosticSurveyRepository;
import com.cesizen.cesizen_back.service.DiagnosticService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiagnosticServiceImpl implements DiagnosticService {

    private final DiagnosticEventRepository eventRepository;
    private final DiagnosticSurveyRepository surveyRepository;


    @Override
    public DiagnosticResponse submit(DiagnosticSubmitRequest request) {

        List<DiagnosticEvent> events = eventRepository.findAll();

        int score = 0;
        List<DiagnosticAnswer> answers = new ArrayList<>();

        DiagnosticSurvey survey = DiagnosticSurvey.builder()
                .userId(request.getUserId())
                .score(0)
                .riskLevel("UNKNOWN")
                .build();

        for (DiagnosticEvent event : events) {

            boolean checked = request.getAnswers().getOrDefault(event.getEventId(), false);

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

        survey = surveyRepository.save(survey);

        return DiagnosticResponse.builder()
                .surveyId(survey.getSurveyId())
                .score(survey.getScore())
                .riskLevel(survey.getRiskLevel())
                .createdAt(survey.getCreatedAt().toString())
                .build();
    }

    @Override
    public List<DiagnosticHistoryResponse> history(String userId) {

        return surveyRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(survey -> DiagnosticHistoryResponse.builder()
                        .surveyId(survey.getSurveyId())
                        .score(survey.getScore())
                        .riskLevel(survey.getRiskLevel())
                        .createdAt(survey.getCreatedAt().toString())
                        .build()
                )
                .toList();
    }

    private String calculateRisk(int score) {
        if (score < 150) return "LOW";
        if (score < 300) return "MEDIUM";
        return "HIGH";
    }
}
