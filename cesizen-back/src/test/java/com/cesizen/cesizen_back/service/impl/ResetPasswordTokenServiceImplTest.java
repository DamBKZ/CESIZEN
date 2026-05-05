package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.entity.ResetPasswordToken;
import com.cesizen.cesizen_back.entity.Role;
import com.cesizen.cesizen_back.entity.User;
import com.cesizen.cesizen_back.repository.ResetPasswordTokenRepository;
import com.cesizen.cesizen_back.repository.UserRepository;
import com.cesizen.cesizen_back.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResetPasswordTokenServiceImplTest {

    @Mock private ResetPasswordTokenRepository resetPasswordTokenRepository;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailService emailService;

    @InjectMocks
    private ResetPasswordTokenServiceImpl resetPasswordTokenService;

    private User user;
    private ResetPasswordToken validToken;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(resetPasswordTokenService, "expirationMinutes", 30);

        user = User.builder()
                .userId("user-123")
                .email("test@test.com")
                .pseudo("testuser")
                .password("encodedPassword")
                .role(Role.builder().roleId(1).roleName("USER").build())
                .active(true)
                .userCreatedAt(LocalDateTime.now())
                .build();

        validToken = ResetPasswordToken.builder()
                .resetPasswordTokenId("reset-token-123")
                .user(user)
                .resetPasswordTokenValue("valid-reset-value")
                .resetPasswordTokenCreatedDate(LocalDateTime.now())
                .resetPasswordTokenEndDate(LocalDateTime.now().plusMinutes(30))
                .used(false)
                .build();
    }

    // =========================================================================
    // CREATE
    // =========================================================================

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("Doit créer un token et supprimer les anciens")
        void shouldCreateToken() {
            when(resetPasswordTokenRepository.save(any(ResetPasswordToken.class)))
                    .thenReturn(validToken);

            ResetPasswordToken result = resetPasswordTokenService.create(user);

            verify(resetPasswordTokenRepository).deleteByUser_UserId("user-123");
            verify(resetPasswordTokenRepository).save(any(ResetPasswordToken.class));
            assertThat(result).isNotNull();
        }
    }

    // =========================================================================
    // CREATE AND SEND BY EMAIL
    // =========================================================================

    @Nested
    @DisplayName("createAndSendByEmail()")
    class CreateAndSendByEmail {

        @Test
        @DisplayName("Doit créer le token et envoyer l'email")
        void shouldCreateAndSendEmail() {
            when(resetPasswordTokenRepository.save(any(ResetPasswordToken.class)))
                    .thenReturn(validToken);

            resetPasswordTokenService.createAndSendByEmail(user);

            verify(emailService).sendResetPasswordEmail(
                    eq("test@test.com"),
                    anyString()
            );
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
            when(resetPasswordTokenRepository.findByResetPasswordTokenValue("valid-reset-value"))
                    .thenReturn(Optional.of(validToken));

            ResetPasswordToken result = resetPasswordTokenService.validate("valid-reset-value");

            assertThat(result.getResetPasswordTokenValue()).isEqualTo("valid-reset-value");
        }

        @Test
        @DisplayName("Doit lever une exception si le token est introuvable")
        void shouldThrowIfTokenNotFound() {
            when(resetPasswordTokenRepository.findByResetPasswordTokenValue("unknown"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> resetPasswordTokenService.validate("unknown"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("introuvable");
        }

        @Test
        @DisplayName("Doit lever une exception si le token est expiré")
        void shouldThrowIfTokenExpired() {
            ResetPasswordToken expiredToken = ResetPasswordToken.builder()
                    .resetPasswordTokenId("reset-expired")
                    .user(user)
                    .resetPasswordTokenValue("expired-value")
                    .resetPasswordTokenCreatedDate(LocalDateTime.now().minusHours(2))
                    .resetPasswordTokenEndDate(LocalDateTime.now().minusMinutes(1))
                    .used(false)
                    .build();

            when(resetPasswordTokenRepository.findByResetPasswordTokenValue("expired-value"))
                    .thenReturn(Optional.of(expiredToken));

            assertThatThrownBy(() -> resetPasswordTokenService.validate("expired-value"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("expiré");
        }

        @Test
        @DisplayName("Doit lever une exception si le token est déjà utilisé")
        void shouldThrowIfTokenAlreadyUsed() {
            ResetPasswordToken usedToken = ResetPasswordToken.builder()
                    .resetPasswordTokenId("reset-used")
                    .user(user)
                    .resetPasswordTokenValue("used-value")
                    .resetPasswordTokenCreatedDate(LocalDateTime.now())
                    .resetPasswordTokenEndDate(LocalDateTime.now().plusMinutes(30))
                    .used(true)
                    .build();

            when(resetPasswordTokenRepository.findByResetPasswordTokenValue("used-value"))
                    .thenReturn(Optional.of(usedToken));

            assertThatThrownBy(() -> resetPasswordTokenService.validate("used-value"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("utilisé");
        }
    }

    // =========================================================================
    // RESET PASSWORD
    // =========================================================================

    @Nested
    @DisplayName("resetPassword()")
    class ResetPassword {

        @Test
        @DisplayName("Doit réinitialiser le mot de passe et marquer le token comme utilisé")
        void shouldResetPassword() {
            when(resetPasswordTokenRepository.findByResetPasswordTokenValue("valid-reset-value"))
                    .thenReturn(Optional.of(validToken));
            when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");

            resetPasswordTokenService.resetPassword("valid-reset-value", "newPassword123");

            assertThat(validToken.isUsed()).isTrue();
            verify(userRepository).save(user);
            verify(resetPasswordTokenRepository).save(validToken);
        }
    }
}