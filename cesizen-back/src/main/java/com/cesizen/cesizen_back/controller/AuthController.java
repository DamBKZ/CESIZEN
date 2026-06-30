package com.cesizen.cesizen_back.controller;

import com.cesizen.cesizen_back.dto.user.ConfirmResetPasswordRequest;
import com.cesizen.cesizen_back.dto.user.LoginRequest;
import com.cesizen.cesizen_back.dto.user.RequestResetPasswordRequest;
import com.cesizen.cesizen_back.security.jwt.JwtService;
import com.cesizen.cesizen_back.service.UserService;
import com.cesizen.cesizen_back.service.RefreshTokenService;
import com.cesizen.cesizen_back.service.ResetPasswordTokenService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {


    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final ResetPasswordTokenService resetPasswordTokenService;

        @Value("${app.cookies.secure:false}")
        private boolean secureCookies;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @Valid @RequestBody LoginRequest request) {

        var user = userService.login(request.getEmail(), request.getPassword());

        var accessToken = jwtService.generateToken(
                user.getUserId(),
                user.getRole().getRoleName()
        );

        var refreshTokenValue = refreshTokenService.create(user).getRefreshTokenValue();

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshTokenValue)
                .httpOnly(true)
                .secure(secureCookies)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite(secureCookies ? "None" : "Lax")
                .build();

        String xsrf = UUID.randomUUID().toString();
        ResponseCookie xsrfCookie = ResponseCookie.from("XSRF-TOKEN", xsrf)
                .httpOnly(false)
                .secure(secureCookies)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite(secureCookies ? "None" : "Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.SET_COOKIE, xsrfCookie.toString())
                .body(Map.of("accessToken", accessToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            @CookieValue(name = "XSRF-TOKEN", required = false) String xsrfCookie,
            @RequestHeader(name = "X-XSRF-TOKEN", required = false) String xsrfHeader) {

        if (refreshToken == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Refresh token manquant"));
        }

        if (xsrfCookie == null || xsrfHeader == null || !xsrfCookie.equals(xsrfHeader)) {
            return ResponseEntity.status(403).body(Map.of("error", "CSRF token invalide"));
        }

        var token = refreshTokenService.validate(refreshToken);

        var newAccessToken = jwtService.generateToken(
                token.getUser().getUserId(),
                token.getUser().getRole().getRoleName()
        );

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            @CookieValue(name = "XSRF-TOKEN", required = false) String xsrfCookie,
            @RequestHeader(name = "X-XSRF-TOKEN", required = false) String xsrfHeader) {

        if (xsrfCookie == null || xsrfHeader == null || !xsrfCookie.equals(xsrfHeader)) {
            return ResponseEntity.status(403).body(Map.of("error", "CSRF token invalide"));
        }

        try {
            if (refreshToken != null) {
                refreshTokenService.revoke(refreshToken);
            }
} catch (Exception e) {
    log.warn("Impossible de révoquer le refresh token lors du logout : {}", e.getMessage());
}


ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
        .httpOnly(true)
        .secure(secureCookies)
        .path("/")
        .maxAge(0)
        .sameSite(secureCookies ? "None" : "Lax")
        .build();

ResponseCookie deleteXsrf = ResponseCookie.from("XSRF-TOKEN", "")
        .httpOnly(false)
        .secure(secureCookies)
        .path("/")
        .maxAge(0)
        .sameSite(secureCookies ? "None" : "Lax")
        .build();


                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                        .header(HttpHeaders.SET_COOKIE, deleteXsrf.toString())
                        .body(Map.of("message", "Déconnexion réussie."));
    }

@PostMapping("/reset-password/request")
public ResponseEntity<Map<String, String>> requestReset(
        @Valid @RequestBody RequestResetPasswordRequest request) {

    userService.findOptionalByEmail(request.getEmail())
            .ifPresent(resetPasswordTokenService::createAndSendByEmail);

    return ResponseEntity.ok(Map.of(
            "message", "Si un compte existe avec cet email, un email de réinitialisation a été envoyé."
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