package com.cesizen.cesizen_back.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DiagnosticHistoryResponse {

    private String surveyId;
    private int score;
    private String riskLevel;
    private String createdAt;
}
