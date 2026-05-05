package com.cesizen.cesizen_back.controller;

import com.cesizen.cesizen_back.dto.user.ConfirmResetPasswordRequest;
import com.cesizen.cesizen_back.dto.user.LoginRequest;
import com.cesizen.cesizen_back.dto.user.RefreshTokenRequest;
import com.cesizen.cesizen_back.dto.user.RequestResetPasswordRequest;
import com.cesizen.cesizen_back.security.jwt.JwtService;
import com.cesizen.cesizen_back.service.UserService;
import com.cesizen.cesizen_back.service.RefreshTokenService;
import com.cesizen.cesizen_back.service.ResetPasswordTokenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final ResetPasswordTokenService resetPasswordTokenService;

    // -------------------------------------------------------------------------
    // LOGIN
    // -------------------------------------------------------------------------

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @Valid @RequestBody LoginRequest request) {

        var user = userService.login(request.getEmail(), request.getPassword());

        var accessToken = jwtService.generateToken(
                user.getUserId(),
                user.getRole().getRoleName()
        );

        var refreshToken = refreshTokenService.create(user).getRefreshTokenValue();

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        ));
    }

    // -------------------------------------------------------------------------
    // REFRESH TOKEN
    // -------------------------------------------------------------------------

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        var token = refreshTokenService.validate(request.getRefreshToken());

        var newAccessToken = jwtService.generateToken(
                token.getUser().getUserId(),
                token.getUser().getRole().getRoleName()
        );

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    // -------------------------------------------------------------------------
    // LOGOUT
    // -------------------------------------------------------------------------

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @Valid @RequestBody RefreshTokenRequest request) {

        refreshTokenService.revoke(request.getRefreshToken());

        return ResponseEntity.ok(Map.of("message", "Déconnexion réussie."));
    }

    // -------------------------------------------------------------------------
    // RESET PASSWORD
    // -------------------------------------------------------------------------

    @PostMapping("/reset-password/request")
    public ResponseEntity<Map<String, String>> requestReset(
            @Valid @RequestBody RequestResetPasswordRequest request) {

        var user = userService.findByEmail(request.getEmail());

        resetPasswordTokenService.createAndSendByEmail(user);

        return ResponseEntity.ok(Map.of(
                "message", "Un email de réinitialisation a été envoyé."
        ));
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<Map<String, String>> confirmReset(
            @Valid @RequestBody ConfirmResetPasswordRequest request) {

        resetPasswordTokenService.resetPassword(
                request.getToken(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(Map.of("message", "Mot de passe mis à jour."));
    }
}