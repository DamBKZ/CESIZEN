package com.cesizen.cesizen_back.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "L'email est obligatoire.")
    @Email(message = "L'email doit être valide.")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Size(min = 12, message = "Le mot de passe doit contenir au moins 12 caractères.")
    private String password;

    @NotBlank(message = "Le pseudo est obligatoire.")
    @Size(min = 2, max = 30, message = "Le pseudo doit contenir entre 2 et 30 caractères.")
    private String pseudo;
}