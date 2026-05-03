package com.cesizen.cesizen_back.repository;

import com.cesizen.cesizen_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPseudo(String pseudo);

    boolean existsByEmail(String email);

    boolean existsByPseudo(String pseudo);

    /**
     * Vérifie si un email est déjà utilisé par un autre utilisateur.
     */
    boolean existsByEmailAndUserIdNot(String email, String userId);

    /**
     * Vérifie si un pseudo est déjà utilisé par un autre utilisateur.
     */
    boolean existsByPseudoAndUserIdNot(String pseudo, String userId);
}