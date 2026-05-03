package com.cesizen.cesizen_back.repository;

import com.cesizen.cesizen_back.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByRefreshTokenValue(String value);

    void deleteByUser_UserId(String userId);

    /**
     * Supprime tous les tokens expirés — à appeler via un job de nettoyage planifié (@Scheduled).
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.refreshTokenEndDate < :now")
    void deleteAllExpired(@Param("now") LocalDateTime now);

    /**
     * Supprime tous les tokens révoqués — utile pour nettoyer après un logout.
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.revoked = true")
    void deleteAllRevoked();
}