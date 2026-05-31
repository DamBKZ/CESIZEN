package com.cesizen.cesizen_back.repository;

import com.cesizen.cesizen_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    @org.springframework.data.jpa.repository.Query("select u from User u join fetch u.role where u.userId = :userId")
    Optional<User> findByUserIdWithRole(String userId);

    @org.springframework.data.jpa.repository.Query("select u from User u join fetch u.role")
    java.util.List<User> findAllWithRole();

    Optional<User> findByPseudo(String pseudo);

    boolean existsByEmail(String email);

    boolean existsByPseudo(String pseudo);
    boolean existsByEmailAndUserIdNot(String email, String userId);
    boolean existsByPseudoAndUserIdNot(String pseudo, String userId);
}