package com.cesizen.cesizen_back.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class DiagnosticSubmitRequest {

    private String userId;

    private Map<String, Boolean> answers;
}
