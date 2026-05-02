package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.entity.RefreshToken;
import com.cesizen.cesizen_back.entity.User;
import com.cesizen.cesizen_back.repository.RefreshTokenRepository;
import com.cesizen.cesizen_back.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration-days:7}")
    private int refreshExpirationDays;

    // -------------------------------------------------------------------------
    // CRÉATION
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public RefreshToken create(User user) {

        // Révoque tous les tokens existants de l'utilisateur avant d'en créer un nouveau
        refreshTokenRepository.deleteByUser_UserId(user.getUserId());

        RefreshToken token = RefreshToken.builder()
                .user(user)
                .refreshTokenValue(UUID.randomUUID().toString())
                .refreshTokenCreatedDate(LocalDateTime.now())
                .refreshTokenEndDate(LocalDateTime.now().plusDays(refreshExpirationDays))
                .build();

        return refreshTokenRepository.save(token);
    }

    // -------------------------------------------------------------------------
    // VALIDATION
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public RefreshToken validate(String value) {

        RefreshToken token = refreshTokenRepository.findByRefreshTokenValue(value)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token introuvable."));

        if (!token.isValid()) {
            throw new IllegalStateException("Refresh token expiré ou révoqué.");
        }

        return token;
    }

    // -------------------------------------------------------------------------
    // RÉVOCATION
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void revoke(String value) {

        RefreshToken token = refreshTokenRepository.findByRefreshTokenValue(value)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token introuvable."));

        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    // -------------------------------------------------------------------------
    // SUPPRESSION
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void deleteByUserId(String userId) {
        refreshTokenRepository.deleteByUser_UserId(userId);
    }
}