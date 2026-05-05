package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.entity.Role;
import com.cesizen.cesizen_back.entity.User;
import com.cesizen.cesizen_back.repository.UserRepository;
import com.cesizen.cesizen_back.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleService roleService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private Role defaultRole;
    private User activeUser;

    @BeforeEach
    void setUp() {
        defaultRole = Role.builder()
                .roleId(1)
                .roleName("USER")
                .build();

        activeUser = User.builder()
                .userId("user-123")
                .email("test@test.com")
                .password("encodedPassword")
                .pseudo("testuser")
                .userCreatedAt(LocalDateTime.now())
                .role(defaultRole)
                .active(true)
                .build();
    }

    // =========================================================================
    // REGISTER
    // =========================================================================

    @Nested
    @DisplayName("register()")
    class Register {

        @Test
        @DisplayName("Doit créer un utilisateur avec les bons champs")
        void shouldRegisterUser() {
            when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
            when(userRepository.existsByPseudo("testuser")).thenReturn(false);
            when(roleService.getDefaultRole()).thenReturn(defaultRole);
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(activeUser);

            User result = userService.register("test@test.com", "password123", "testuser");

            assertThat(result.getEmail()).isEqualTo("test@test.com");
            assertThat(result.getPseudo()).isEqualTo("testuser");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Doit lever une exception si l'email existe déjà")
        void shouldThrowIfEmailExists() {
            when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

            assertThatThrownBy(() -> userService.register("test@test.com", "password123", "testuser"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("email");
        }

        @Test
        @DisplayName("Doit lever une exception si le pseudo existe déjà")
        void shouldThrowIfPseudoExists() {
            when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
            when(userRepository.existsByPseudo("testuser")).thenReturn(true);

            assertThatThrownBy(() -> userService.register("test@test.com", "password123", "testuser"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("pseudo");
        }
    }

    // =========================================================================
    // LOGIN
    // =========================================================================

    @Nested
    @DisplayName("login()")
    class Login {

        @Test
        @DisplayName("Doit retourner l'utilisateur si les identifiants sont corrects")
        void shouldLoginSuccessfully() {
            when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(activeUser));
            when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

            User result = userService.login("test@test.com", "password123");

            assertThat(result.getEmail()).isEqualTo("test@test.com");
        }

        @Test
        @DisplayName("Doit lever une exception si l'email est introuvable")
        void shouldThrowIfEmailNotFound() {
            when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.login("unknown@test.com", "password123"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Identifiants invalides");
        }

        @Test
        @DisplayName("Doit lever une exception si le mot de passe est incorrect")
        void shouldThrowIfPasswordWrong() {
            when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(activeUser));
            when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

            assertThatThrownBy(() -> userService.login("test@test.com", "wrongPassword"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Identifiants invalides");
        }

        @Test
        @DisplayName("Doit lever une exception si le compte est désactivé")
        void shouldThrowIfAccountDisabled() {
            User disabledUser = User.builder()
                    .userId("user-456")
                    .email("test@test.com")
                    .password("encodedPassword")
                    .pseudo("testuser")
                    .userCreatedAt(LocalDateTime.now())
                    .role(defaultRole)
                    .active(false)
                    .build();

            when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(disabledUser));

            assertThatThrownBy(() -> userService.login("test@test.com", "password123"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("désactivé");
        }
    }

    // =========================================================================
    // CHANGE PASSWORD
    // =========================================================================

    @Nested
    @DisplayName("changePassword()")
    class ChangePassword {

        @Test
        @DisplayName("Doit changer le mot de passe avec succès")
        void shouldChangePasswordSuccessfully() {
            when(userRepository.findById("user-123")).thenReturn(Optional.of(activeUser));
            when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
            when(passwordEncoder.matches("newPassword123", "encodedPassword")).thenReturn(false);
            when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");

            userService.changePassword("user-123", "password123", "newPassword123");

            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Doit lever une exception si le mot de passe actuel est incorrect")
        void shouldThrowIfCurrentPasswordWrong() {
            when(userRepository.findById("user-123")).thenReturn(Optional.of(activeUser));
            when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

            assertThatThrownBy(() -> userService.changePassword("user-123", "wrongPassword", "newPassword123"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("actuel");
        }

        @Test
        @DisplayName("Doit lever une exception si le nouveau mot de passe est identique à l'actuel")
        void shouldThrowIfNewPasswordSameAsCurrent() {
            when(userRepository.findById("user-123")).thenReturn(Optional.of(activeUser));
            when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
            when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

            assertThatThrownBy(() -> userService.changePassword("user-123", "password123", "password123"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("différent");
        }
    }

    // =========================================================================
    // ADMIN
    // =========================================================================

    @Nested
    @DisplayName("deactivate()")
    class Deactivate {

        @Test
        @DisplayName("Doit désactiver un compte actif")
        void shouldDeactivateActiveUser() {
            when(userRepository.findById("user-123")).thenReturn(Optional.of(activeUser));

            userService.deactivate("user-123");

            assertThat(activeUser.isActive()).isFalse();
            verify(userRepository).save(activeUser);
        }

        @Test
        @DisplayName("Doit lever une exception si le compte est déjà désactivé")
        void shouldThrowIfAlreadyDeactivated() {
            activeUser.setActive(false);
            when(userRepository.findById("user-123")).thenReturn(Optional.of(activeUser));

            assertThatThrownBy(() -> userService.deactivate("user-123"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("déjà désactivé");
        }
    }

    @Nested
    @DisplayName("activate()")
    class Activate {

        @Test
        @DisplayName("Doit activer un compte désactivé")
        void shouldActivateDisabledUser() {
            activeUser.setActive(false);
            when(userRepository.findById("user-123")).thenReturn(Optional.of(activeUser));

            userService.activate("user-123");

            assertThat(activeUser.isActive()).isTrue();
            verify(userRepository).save(activeUser);
        }

        @Test
        @DisplayName("Doit lever une exception si le compte est déjà actif")
        void shouldThrowIfAlreadyActive() {
            when(userRepository.findById("user-123")).thenReturn(Optional.of(activeUser));

            assertThatThrownBy(() -> userService.activate("user-123"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("déjà actif");
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("Doit supprimer un utilisateur existant")
        void shouldDeleteUser() {
            when(userRepository.findById("user-123")).thenReturn(Optional.of(activeUser));

            userService.delete("user-123");

            verify(userRepository).delete(activeUser);
        }

        @Test
        @DisplayName("Doit lever une exception si l'utilisateur est introuvable")
        void shouldThrowIfUserNotFound() {
            when(userRepository.findById("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.delete("unknown"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("introuvable");
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("Doit retourner la liste de tous les utilisateurs")
        void shouldReturnAllUsers() {
            when(userRepository.findAll()).thenReturn(List.of(activeUser));

            List<User> result = userService.findAll();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getEmail()).isEqualTo("test@test.com");
        }
    }
}