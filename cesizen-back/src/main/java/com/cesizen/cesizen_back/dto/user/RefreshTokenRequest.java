package com.cesizen.cesizen_back.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequest {

    @NotBlank(message = "Le refresh token est obligatoire.")
    private String refreshToken;
}