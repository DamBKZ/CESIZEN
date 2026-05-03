package com.cesizen.cesizen_back.repository;

import com.cesizen.cesizen_back.entity.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, String> {

    Optional<ResetPasswordToken> findByResetPasswordTokenValue(String value);

    void deleteByUser_UserId(String userId);

    /**
     * Supprime tous les tokens expirés — à appeler via un job de nettoyage planifié (@Scheduled).
     */
    @Modifying
    @Query("DELETE FROM ResetPasswordToken rt WHERE rt.resetPasswordTokenEndDate < :now")
    void deleteAllExpired(@Param("now") LocalDateTime now);

    /**
     * Supprime tous les tokens déjà consommés — utile après une réinitialisation de mot de passe.
     */
    @Modifying
    @Query("DELETE FROM ResetPasswordToken rt WHERE rt.used = true")
    void deleteAllUsed();
}