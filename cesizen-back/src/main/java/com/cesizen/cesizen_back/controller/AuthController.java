package com.cesizen.cesizen_back.controller;

import com.cesizen.cesizen_back.dto.user.ConfirmResetPasswordRequest;
import com.cesizen.cesizen_back.dto.user.LoginRequest;
import com.cesizen.cesizen_back.dto.user.RequestResetPasswordRequest;
import com.cesizen.cesizen_back.security.jwt.JwtService;
import com.cesizen.cesizen_back.service.RefreshTokenService;
import com.cesizen.cesizen_back.service.ResetPasswordTokenService;
import com.cesizen.cesizen_back.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH_COOKIE_NAME = "refreshToken";
    private static final String XSRF_COOKIE_NAME = "XSRF-TOKEN";
    private static final String XSRF_HEADER_NAME = "X-XSRF-TOKEN";

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final ResetPasswordTokenService resetPasswordTokenService;

    @Value("${app.cookies.secure:true}")
    private boolean secureCookies;

    @Value("${jwt.refresh-expiration-days:7}")
    private long refreshExpirationDays;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        var user = userService.login(
                request.getEmail(),
                request.getPassword()
        );

        String accessToken = jwtService.generateToken(
                user.getUserId(),
                user.getRole().getRoleName()
        );

        String refreshTokenValue = refreshTokenService
                .create(user)
                .getRefreshTokenValue();

        String xsrfToken = UUID.randomUUID().toString();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        createRefreshCookie(refreshTokenValue).toString()
                )
                .header(
                        HttpHeaders.SET_COOKIE,
                        createXsrfCookie(xsrfToken).toString()
                )
                .body(Map.of("accessToken", accessToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(
            @CookieValue(
                    name = REFRESH_COOKIE_NAME,
                    required = false
            ) String refreshToken,
            @CookieValue(
                    name = XSRF_COOKIE_NAME,
                    required = false
            ) String xsrfCookie,
            @RequestHeader(
                    name = XSRF_HEADER_NAME,
                    required = false
            ) String xsrfHeader
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity
                    .status(401)
                    .body(Map.of(
                            "error",
                            "Refresh token manquant."
                    ));
        }

        if (!isValidXsrfToken(xsrfCookie, xsrfHeader)) {
            return ResponseEntity
                    .status(403)
                    .body(Map.of(
                            "error",
                            "CSRF token invalide."
                    ));
        }

        var token = refreshTokenService.validate(refreshToken);
        var user = token.getUser();

        String newAccessToken = jwtService.generateToken(
                user.getUserId(),
                user.getRole().getRoleName()
        );

        return ResponseEntity.ok(
                Map.of("accessToken", newAccessToken)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @CookieValue(
                    name = REFRESH_COOKIE_NAME,
                    required = false
            ) String refreshToken,
            @CookieValue(
                    name = XSRF_COOKIE_NAME,
                    required = false
            ) String xsrfCookie,
            @RequestHeader(
                    name = XSRF_HEADER_NAME,
                    required = false
            ) String xsrfHeader
    ) {
        if (!isValidXsrfToken(xsrfCookie, xsrfHeader)) {
            return ResponseEntity
                    .status(403)
                    .body(Map.of(
                            "error",
                            "CSRF token invalide."
                    ));
        }

        try {
            if (refreshToken != null && !refreshToken.isBlank()) {
                refreshTokenService.revoke(refreshToken);
            }
        } catch (Exception exception) {
            /*
             * La déconnexion locale doit rester possible même si le token
             * n'existe plus ou si sa révocation échoue.
             */
            log.warn(
                    "Impossible de révoquer le refresh token lors du logout : {}",
                    exception.getMessage()
            );
        }

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        deleteRefreshCookie().toString()
                )
                .header(
                        HttpHeaders.SET_COOKIE,
                        deleteXsrfCookie().toString()
                )
                .body(Map.of(
                        "message",
                        "Déconnexion réussie."
                ));
    }

    @PostMapping("/reset-password/request")
    public ResponseEntity<Map<String, String>> requestReset(
            @Valid @RequestBody RequestResetPasswordRequest request
    ) {
        userService.findOptionalByEmail(request.getEmail())
                .ifPresent(
                        resetPasswordTokenService::createAndSendByEmail
                );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Si un compte existe avec cet email, "
                                + "un email de réinitialisation a été envoyé."
                )
        );
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<Map<String, String>> confirmReset(
            @Valid @RequestBody ConfirmResetPasswordRequest request
    ) {
        resetPasswordTokenService.resetPassword(
                request.getToken(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Mot de passe mis à jour."
                )
        );
    }

    private ResponseCookie createRefreshCookie(String value) {
        return ResponseCookie
                .from(REFRESH_COOKIE_NAME, value)
                .httpOnly(true)
                .secure(secureCookies)
                .path("/")
                .maxAge(Duration.ofDays(refreshExpirationDays))
                .sameSite(cookieSameSite())
                .build();
    }

    private ResponseCookie createXsrfCookie(String value) {
        return ResponseCookie
                .from(XSRF_COOKIE_NAME, value)
                .httpOnly(false)
                .secure(secureCookies)
                .path("/")
                .maxAge(Duration.ofDays(refreshExpirationDays))
                .sameSite(cookieSameSite())
                .build();
    }

    private ResponseCookie deleteRefreshCookie() {
        return ResponseCookie
                .from(REFRESH_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(secureCookies)
                .path("/")
                .maxAge(Duration.ZERO)
                .sameSite(cookieSameSite())
                .build();
    }

    private ResponseCookie deleteXsrfCookie() {
        return ResponseCookie
                .from(XSRF_COOKIE_NAME, "")
                .httpOnly(false)
                .secure(secureCookies)
                .path("/")
                .maxAge(Duration.ZERO)
                .sameSite(cookieSameSite())
                .build();
    }

    private String cookieSameSite() {
        return secureCookies ? "None" : "Lax";
    }

    private boolean isValidXsrfToken(
            String cookieValue,
            String headerValue
    ) {
        if (
                cookieValue == null
                || cookieValue.isBlank()
                || headerValue == null
                || headerValue.isBlank()
        ) {
            return false;
        }

        return MessageDigest.isEqual(
                cookieValue.getBytes(StandardCharsets.UTF_8),
                headerValue.getBytes(StandardCharsets.UTF_8)
        );
    }
}
