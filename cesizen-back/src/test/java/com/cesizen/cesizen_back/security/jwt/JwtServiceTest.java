package com.cesizen.cesizen_back.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET =
            "dGVzdHNlY3JldGtleWZvcnVuaXR0ZXN0c29ubHkxMjM0NTY3ODkwYWJjZGVmZ2hpams";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L);
    }

    // =========================================================================
    // GENERATE + EXTRACT
    // =========================================================================

    @Nested
    @DisplayName("generateToken() + extract*()")
    class GenerateAndExtract {

        @Test
        @DisplayName("Doit générer un token et en extraire le userId")
        void shouldExtractUserId() {
            String token = jwtService.generateToken("user-123", "USER");

            assertThat(jwtService.extractUserId(token)).isEqualTo("user-123");
        }

        @Test
        @DisplayName("Doit générer un token et en extraire le rôle")
        void shouldExtractRole() {
            String token = jwtService.generateToken("user-123", "ADMIN");

            assertThat(jwtService.extractRole(token)).isEqualTo("ADMIN");
        }
    }

    // =========================================================================
    // VALIDATION
    // =========================================================================

    @Nested
    @DisplayName("isTokenValid()")
    class IsTokenValid {

        @Test
        @DisplayName("Doit retourner true pour un token valide")
        void shouldReturnTrueForValidToken() {
            String token = jwtService.generateToken("user-123", "USER");

            assertThat(jwtService.isTokenValid(token)).isTrue();
        }

        @Test
        @DisplayName("Doit retourner false pour un token invalide")
        void shouldReturnFalseForInvalidToken() {
            assertThat(jwtService.isTokenValid("token.invalide.ici")).isFalse();
        }

        @Test
        @DisplayName("Doit retourner false pour un token expiré")
        void shouldReturnFalseForExpiredToken() {
            ReflectionTestUtils.setField(jwtService, "expirationMs", -1000L);
            String expiredToken = jwtService.generateToken("user-123", "USER");

            assertThat(jwtService.isTokenValid(expiredToken)).isFalse();
        }
    }
}