package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.entity.RefreshToken;
import com.cesizen.cesizen_back.entity.Role;
import com.cesizen.cesizen_back.entity.User;
import com.cesizen.cesizen_back.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private User user;
    private RefreshToken validToken;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshExpirationDays", 7);

        user = User.builder()
                .userId("user-123")
                .email("test@test.com")
                .pseudo("testuser")
                .role(Role.builder().roleId(1).roleName("USER").build())
                .active(true)
                .userCreatedAt(LocalDateTime.now())
                .build();

        validToken = RefreshToken.builder()
                .refreshTokenId("token-123")
                .user(user)
                .refreshTokenValue("valid-token-value")
                .refreshTokenCreatedDate(LocalDateTime.now())
                .refreshTokenEndDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();
    }

    // =========================================================================
    // CREATE
    // =========================================================================

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("Doit créer un refresh token et supprimer les anciens")
        void shouldCreateRefreshToken() {
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(validToken);

            RefreshToken result = refreshTokenService.create(user);

            verify(refreshTokenRepository).deleteByUser_UserId("user-123");
            verify(refreshTokenRepository).save(any(RefreshToken.class));
            assertThat(result).isNotNull();
        }
    }

    // =========================================================================
    // VALIDATE
    // =========================================================================

    @Nested
    @DisplayName("validate()")
    class Validate {

        @Test
        @DisplayName("Doit retourner le token si valide")
        void shouldReturnValidToken() {
            when(refreshTokenRepository.findByRefreshTokenValue("valid-token-value"))
                    .thenReturn(Optional.of(validToken));

            RefreshToken result = refreshTokenService.validate("valid-token-value");

            assertThat(result.getRefreshTokenValue()).isEqualTo("valid-token-value");
        }

        @Test
        @DisplayName("Doit lever une exception si le token est introuvable")
        void shouldThrowIfTokenNotFound() {
            when(refreshTokenRepository.findByRefreshTokenValue("unknown"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> refreshTokenService.validate("unknown"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("introuvable");
        }

        @Test
        @DisplayName("Doit lever une exception si le token est expiré")
        void shouldThrowIfTokenExpired() {
            RefreshToken expiredToken = RefreshToken.builder()
                    .refreshTokenId("token-expired")
                    .user(user)
                    .refreshTokenValue("expired-token")
                    .refreshTokenCreatedDate(LocalDateTime.now().minusDays(8))
                    .refreshTokenEndDate(LocalDateTime.now().minusDays(1))
                    .revoked(false)
                    .build();

            when(refreshTokenRepository.findByRefreshTokenValue("expired-token"))
                    .thenReturn(Optional.of(expiredToken));

            assertThatThrownBy(() -> refreshTokenService.validate("expired-token"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("expiré");
        }

        @Test
        @DisplayName("Doit lever une exception si le token est révoqué")
        void shouldThrowIfTokenRevoked() {
            RefreshToken revokedToken = RefreshToken.builder()
                    .refreshTokenId("token-revoked")
                    .user(user)
                    .refreshTokenValue("revoked-token")
                    .refreshTokenCreatedDate(LocalDateTime.now())
                    .refreshTokenEndDate(LocalDateTime.now().plusDays(7))
                    .revoked(true)
                    .build();

            when(refreshTokenRepository.findByRefreshTokenValue("revoked-token"))
                    .thenReturn(Optional.of(revokedToken));

            assertThatThrownBy(() -> refreshTokenService.validate("revoked-token"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("révoqué");
        }
    }

    // =========================================================================
    // REVOKE
    // =========================================================================

    @Nested
    @DisplayName("revoke()")
    class Revoke {

        @Test
        @DisplayName("Doit révoquer un token valide")
        void shouldRevokeToken() {
            when(refreshTokenRepository.findByRefreshTokenValue("valid-token-value"))
                    .thenReturn(Optional.of(validToken));

            refreshTokenService.revoke("valid-token-value");

            assertThat(validToken.isRevoked()).isTrue();
            verify(refreshTokenRepository).save(validToken);
        }

        @Test
        @DisplayName("Doit lever une exception si le token est introuvable")
        void shouldThrowIfTokenNotFound() {
            when(refreshTokenRepository.findByRefreshTokenValue("unknown"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> refreshTokenService.revoke("unknown"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("introuvable");
        }
    }
}