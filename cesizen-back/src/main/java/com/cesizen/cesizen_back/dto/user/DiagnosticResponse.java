package com.cesizen.cesizen_back.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class DiagnosticResponse {

    private String surveyId;
    private int score;
    private String riskLevel;
    private String createdAt;
}
