package com.cesizen.cesizen_back.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class DiagnosticSubmitRequest {

    @NotNull(message = "Les réponses du diagnostic sont obligatoires.")
    private Map<String, Boolean> answers;
}
