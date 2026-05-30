package com.cesizen.cesizen_back.controller;

import com.cesizen.cesizen_back.dto.user.ChangePasswordRequest;
import com.cesizen.cesizen_back.dto.user.RegisterRequest;
import com.cesizen.cesizen_back.dto.user.RoleResponse;
import com.cesizen.cesizen_back.dto.user.UpdateUserRequest;
import com.cesizen.cesizen_back.dto.user.UserResponse;
import com.cesizen.cesizen_back.entity.User;
import com.cesizen.cesizen_back.service.EmailService;
import com.cesizen.cesizen_back.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

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

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        User user = extractUser(authentication);
        return ResponseEntity.ok(toResponse(user));
    }

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

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteMyAccount(
            @AuthenticationPrincipal User user) {

        userService.deleteUser(user.getUserId());

        emailService.sendAccountDeletionEmail(user.getEmail(), user.getPseudo());

        return ResponseEntity.noContent().build();
    }

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
                .role(RoleResponse.builder()
                        .roleId(user.getRole().getRoleId())
                        .roleName(user.getRole().getRoleName())
                        .build())
                .userCreatedAt(user.getUserCreatedAt().toString())
                .build();
    }
}