package com.cesizen.cesizen_back.dto.user;

import jakarta.validation.constraints.NotBlank;

public record AdviceRequest(
        @NotBlank(message = "Le niveau du conseil est obligatoire.")
        String level,

        @NotBlank(message = "Le message du conseil est obligatoire.")
        String message
) {}
