package com.cesizen.cesizen_back.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET =
            "test-secret-key-for-unit-tests-only-1234567890";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = createJwtService(3_600_000L);
    }

    private JwtService createJwtService(long expirationMs) {
        JwtService service = new JwtService();

        ReflectionTestUtils.setField(
                service,
                "secret",
                SECRET
        );

        ReflectionTestUtils.setField(
                service,
                "expirationMs",
                expirationMs
        );

        /*
         * @PostConstruct n'est pas exécuté, car le service est instancié
         * directement sans contexte Spring.
         */
        service.initializeSigningKey();

        return service;
    }

    @Nested
    @DisplayName("generateToken() + extract*()")
    class GenerateAndExtract {

        @Test
        @DisplayName("Doit générer un token et en extraire le userId")
        void shouldExtractUserId() {
            String token = jwtService.generateToken(
                    "user-123",
                    "USER"
            );

            assertThat(jwtService.extractUserId(token))
                    .isEqualTo("user-123");
        }

        @Test
        @DisplayName("Doit générer un token et en extraire le rôle")
        void shouldExtractRole() {
            String token = jwtService.generateToken(
                    "user-123",
                    "ADMIN"
            );

            assertThat(jwtService.extractRole(token))
                    .isEqualTo("ADMIN");
        }
    }

    @Nested
    @DisplayName("isTokenValid()")
    class IsTokenValid {

        @Test
        @DisplayName("Doit retourner true pour un token valide")
        void shouldReturnTrueForValidToken() {
            String token = jwtService.generateToken(
                    "user-123",
                    "USER"
            );

            assertThat(jwtService.isTokenValid(token))
                    .isTrue();
        }

        @Test
        @DisplayName("Doit retourner false pour un token invalide")
        void shouldReturnFalseForInvalidToken() {
            assertThat(
                    jwtService.isTokenValid("token.invalide.ici")
            ).isFalse();
        }

        @Test
        @DisplayName("Doit retourner false pour un token expiré")
        void shouldReturnFalseForExpiredToken()
                throws InterruptedException {

            JwtService shortLivedJwtService =
                    createJwtService(1L);

            String expiredToken =
                    shortLivedJwtService.generateToken(
                            "user-123",
                            "USER"
                    );

            /*
             * On attend que la durée de validité de 1 ms soit dépassée.
             */
            Thread.sleep(10L);

            assertThat(
                    shortLivedJwtService.isTokenValid(expiredToken)
            ).isFalse();
        }
    }
}
