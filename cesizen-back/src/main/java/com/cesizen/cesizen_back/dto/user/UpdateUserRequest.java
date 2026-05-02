package com.cesizen.cesizen_back.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    @NotBlank(message = "L'email est obligatoire.")
    @Email(message = "L'email doit être valide.")
    private String email;

    @NotBlank(message = "Le pseudo est obligatoire.")
    @Size(min = 2, max = 30, message = "Le pseudo doit contenir entre 2 et 30 caractères.")
    private String pseudo;
}