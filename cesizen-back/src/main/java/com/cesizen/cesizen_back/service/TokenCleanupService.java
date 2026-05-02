package com.cesizen.cesizen_back.service;

import com.cesizen.cesizen_back.repository.RefreshTokenRepository;
import com.cesizen.cesizen_back.repository.ResetPasswordTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenCleanupService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;

    /**
     * Nettoyage automatique des tokens expirés, révoqués et consommés.
     * Exécuté tous les jours à 3h du matin.
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanTokens() {
        LocalDateTime now = LocalDateTime.now();

        refreshTokenRepository.deleteAllExpired(now);
        refreshTokenRepository.deleteAllRevoked();
        resetPasswordTokenRepository.deleteAllExpired(now);
        resetPasswordTokenRepository.deleteAllUsed();

        log.info("Nettoyage des tokens effectué à {}", now);
    }
}