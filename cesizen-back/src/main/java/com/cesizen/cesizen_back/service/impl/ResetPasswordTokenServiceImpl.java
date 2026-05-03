package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.entity.ResetPasswordToken;
import com.cesizen.cesizen_back.entity.User;
import com.cesizen.cesizen_back.repository.ResetPasswordTokenRepository;
import com.cesizen.cesizen_back.repository.UserRepository;
import com.cesizen.cesizen_back.service.EmailService;
import com.cesizen.cesizen_back.service.ResetPasswordTokenService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResetPasswordTokenServiceImpl implements ResetPasswordTokenService {

    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${reset-password.expiration-minutes:30}")
    private int expirationMinutes;

    // -------------------------------------------------------------------------
    // CRÉATION
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ResetPasswordToken create(User user) {

        resetPasswordTokenRepository.deleteByUser_UserId(user.getUserId());

        ResetPasswordToken token = ResetPasswordToken.builder()
                .user(user)
                .resetPasswordTokenValue(UUID.randomUUID().toString())
                .resetPasswordTokenCreatedDate(LocalDateTime.now())
                .resetPasswordTokenEndDate(LocalDateTime.now().plusMinutes(expirationMinutes))
                .build();

        return resetPasswordTokenRepository.save(token);
    }

    @Override
    @Transactional
    public void createAndSendByEmail(User user) {
        ResetPasswordToken token = create(user);
        emailService.sendResetPasswordEmail(user.getEmail(), token.getResetPasswordTokenValue());
    }

    // -------------------------------------------------------------------------
    // VALIDATION
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public ResetPasswordToken validate(String value) {

        ResetPasswordToken token = resetPasswordTokenRepository
                .findByResetPasswordTokenValue(value)
                .orElseThrow(() -> new IllegalArgumentException("Token de réinitialisation introuvable."));

        if (!token.isValid()) {
            throw new IllegalStateException("Token de réinitialisation expiré ou déjà utilisé.");
        }

        return token;
    }

    // -------------------------------------------------------------------------
    // RESET
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void resetPassword(String tokenValue, String newPassword) {

        ResetPasswordToken token = validate(tokenValue);

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        token.setUsed(true);
        resetPasswordTokenRepository.save(token);
    }
}