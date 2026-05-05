package com.cesizen.cesizen_back.controller;

import com.cesizen.cesizen_back.dto.user.ChangePasswordRequest;
import com.cesizen.cesizen_back.dto.user.RegisterRequest;
import com.cesizen.cesizen_back.dto.user.UpdateUserRequest;
import com.cesizen.cesizen_back.dto.user.UserResponse;
import com.cesizen.cesizen_back.entity.User;
import com.cesizen.cesizen_back.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // -------------------------------------------------------------------------
    // REGISTER
    // -------------------------------------------------------------------------

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        User user = userService.register(
                request.getEmail(),
                request.getPassword(),
                request.getPseudo()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(user));
    }

    // -------------------------------------------------------------------------
    // GET PROFIL
    // -------------------------------------------------------------------------

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        User user = extractUser(authentication);
        return ResponseEntity.ok(toResponse(user));
    }

    // -------------------------------------------------------------------------
    // UPDATE PROFIL
    // -------------------------------------------------------------------------

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMe(
            Authentication authentication,
            @Valid @RequestBody UpdateUserRequest request) {

        User user = extractUser(authentication);

        user = userService.updateUserProfile(
                user.getUserId(),
                request.getEmail(),
                request.getPseudo()
        );

        return ResponseEntity.ok(toResponse(user));
    }

    // -------------------------------------------------------------------------
    // CHANGEMENT DE MOT DE PASSE
    // -------------------------------------------------------------------------

    @PutMapping("/me/password")
    public ResponseEntity<Map<String, String>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {

        User user = extractUser(authentication);

        userService.changePassword(
                user.getUserId(),
                request.getCurrentPassword(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(Map.of("message", "Mot de passe mis à jour."));
    }

    // -------------------------------------------------------------------------
    // HELPERS PRIVÉS
    // -------------------------------------------------------------------------

    private User extractUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new IllegalStateException("Utilisateur non authentifié ou principal invalide.");
        }
        return user;
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .pseudo(user.getPseudo())
                .userCreatedAt(user.getUserCreatedAt().toString())
                .build();
    }
}