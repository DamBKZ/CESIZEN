package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.entity.User;
import com.cesizen.cesizen_back.exception.BadRequestException;
import com.cesizen.cesizen_back.exception.NotFoundException;
import com.cesizen.cesizen_back.repository.UserRepository;
import com.cesizen.cesizen_back.service.RoleService;
import com.cesizen.cesizen_back.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findOptionalByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }

        return userRepository.findByEmail(normalizeEmail(email));
    }

    @Override
    @Transactional
    public User register(
            String email,
            String password,
            String pseudo
    ) {
        String normalizedEmail = normalizeEmail(email);
        String normalizedPseudo = normalizePseudo(pseudo);

        validatePassword(password);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException(
                    "Un compte existe déjà avec cet email."
            );
        }

        if (userRepository.existsByPseudo(normalizedPseudo)) {
            throw new BadRequestException(
                    "Ce pseudo est déjà utilisé."
            );
        }

        User user = User.builder()
                .email(normalizedEmail)
                .password(passwordEncoder.encode(password))
                .pseudo(normalizedPseudo)
                .role(roleService.getDefaultRole())
                .build();

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User login(
            String email,
            String rawPassword
    ) {
        String normalizedEmail = normalizeEmail(email);

        if (rawPassword == null || rawPassword.isBlank()) {
            throw new BadRequestException("Identifiants invalides.");
        }

        User user = userRepository
                .findByEmailWithRole(normalizedEmail)
                .orElseThrow(() ->
                        new BadRequestException("Identifiants invalides.")
                );

        if (!user.isEnabled()) {
            throw new IllegalStateException(
                    "Ce compte est désactivé."
            );
        }

        if (!passwordEncoder.matches(
                rawPassword,
                user.getPassword()
        )) {
            throw new BadRequestException("Identifiants invalides.");
        }

        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository
                .findByEmailWithRole(normalizeEmail(email))
                .orElseThrow(() ->
                        new NotFoundException("Utilisateur introuvable.")
                );
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(String userId) {
        validateUserId(userId);

        return userRepository
                .findByUserIdWithRole(userId)
                .orElseThrow(() ->
                        new NotFoundException("Utilisateur introuvable.")
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAllWithRole();
    }

    @Override
    @Transactional
    public User updateUserProfile(
            String userId,
            String newEmail,
            String newPseudo
    ) {
        validateUserId(userId);

        String normalizedEmail = normalizeEmail(newEmail);
        String normalizedPseudo = normalizePseudo(newPseudo);

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("Utilisateur introuvable.")
                );

        boolean emailChanged =
                !user.getEmail().equals(normalizedEmail);

        if (
                emailChanged
                && userRepository.existsByEmailAndUserIdNot(
                        normalizedEmail,
                        userId
                )
        ) {
            throw new BadRequestException(
                    "Un compte existe déjà avec cet email."
            );
        }

        boolean pseudoChanged =
                !user.getPseudo().equals(normalizedPseudo);

        if (
                pseudoChanged
                && userRepository.existsByPseudoAndUserIdNot(
                        normalizedPseudo,
                        userId
                )
        ) {
            throw new BadRequestException(
                    "Ce pseudo est déjà utilisé."
            );
        }

        user.setEmail(normalizedEmail);
        user.setPseudo(normalizedPseudo);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(
            String userId,
            String currentPassword,
            String newPassword
    ) {
        validateUserId(userId);

        if (currentPassword == null || currentPassword.isBlank()) {
            throw new BadRequestException(
                    "Le mot de passe actuel est obligatoire."
            );
        }

        validatePassword(newPassword);

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("Utilisateur introuvable.")
                );

        if (!passwordEncoder.matches(
                currentPassword,
                user.getPassword()
        )) {
            throw new BadRequestException(
                    "Le mot de passe actuel est incorrect."
            );
        }

        if (passwordEncoder.matches(
                newPassword,
                user.getPassword()
        )) {
            throw new BadRequestException(
                    "Le nouveau mot de passe doit être différent de l'actuel."
            );
        }

        user.setPassword(passwordEncoder.encode(newPassword));
    }

    @Override
    @Transactional
    public void deactivate(String userId) {
        User user = findManagedUser(userId);

        if (!user.isActive()) {
            throw new IllegalStateException(
                    "Ce compte est déjà désactivé."
            );
        }

        user.setActive(false);
    }

    @Override
    @Transactional
    public void activate(String userId) {
        User user = findManagedUser(userId);

        if (user.isActive()) {
            throw new IllegalStateException(
                    "Ce compte est déjà actif."
            );
        }

        user.setActive(true);
    }

    @Override
    @Transactional
    public void delete(String userId) {
        User user = findManagedUser(userId);
        userRepository.delete(user);
    }

    private User findManagedUser(String userId) {
        validateUserId(userId);

        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("Utilisateur introuvable.")
                );
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException(
                    "L'adresse email est obligatoire."
            );
        }

        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private String normalizePseudo(String pseudo) {
        if (pseudo == null || pseudo.isBlank()) {
            throw new BadRequestException(
                    "Le pseudo est obligatoire."
            );
        }

        return pseudo.trim();
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new BadRequestException(
                    "Le mot de passe est obligatoire."
            );
        }
    }

    private void validateUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new BadRequestException(
                    "L'identifiant utilisateur est obligatoire."
            );
        }
    }
}
