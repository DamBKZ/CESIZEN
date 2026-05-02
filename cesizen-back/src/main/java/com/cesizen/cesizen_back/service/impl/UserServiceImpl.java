package com.cesizen.cesizen_back.service.impl;


import com.cesizen.cesizen_back.entity.User;
import com.cesizen.cesizen_back.service.RoleService;
import com.cesizen.cesizen_back.service.UserService;
import com.cesizen.cesizen_back.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    // -------------------------------------------------------------------------
    // INSCRIPTION
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public User register(String email, String password, String pseudo) {

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Un compte existe déjà avec cet email.");
        }

        if (userRepository.existsByPseudo(pseudo)) {
            throw new IllegalArgumentException("Ce pseudo est déjà utilisé.");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .pseudo(pseudo)
                .userCreatedAt(LocalDateTime.now())
                .role(roleService.getDefaultRole())
                .build();

        return userRepository.save(user);
    }

    // -------------------------------------------------------------------------
    // CONNEXION
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public User login(String email, String rawPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Identifiants invalides."));

        if (!user.isEnabled()) {
            throw new IllegalStateException("Ce compte est désactivé.");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Identifiants invalides.");
        }

        return user;
    }

    // -------------------------------------------------------------------------
    // RECHERCHE
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));
    }

    // -------------------------------------------------------------------------
    // MISE À JOUR
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public User updateUserProfile(String userId, String newEmail, String newPseudo) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

        if (!user.getEmail().equals(newEmail) && userRepository.existsByEmailAndUserIdNot(newEmail, userId)) {
            throw new IllegalArgumentException("Un compte existe déjà avec cet email.");
        }

        if (!user.getPseudo().equals(newPseudo) && userRepository.existsByPseudoAndUserIdNot(newPseudo, userId)) {
            throw new IllegalArgumentException("Ce pseudo est déjà utilisé.");
        }

        user.setEmail(newEmail);
        user.setPseudo(newPseudo);

        return userRepository.save(user);
    }
}