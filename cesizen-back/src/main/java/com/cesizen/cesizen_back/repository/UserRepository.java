package com.cesizen.cesizen_back.repository;

import com.cesizen.cesizen_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPseudo(String pseudo);

    @Query("""
            SELECT user
            FROM User user
            JOIN FETCH user.role
            WHERE user.userId = :userId
            """)
    Optional<User> findByUserIdWithRole(
            @Param("userId") String userId
    );

    @Query("""
            SELECT user
            FROM User user
            JOIN FETCH user.role
            WHERE user.email = :email
            """)
    Optional<User> findByEmailWithRole(
            @Param("email") String email
    );

    @Query("""
            SELECT user
            FROM User user
            JOIN FETCH user.role
            """)
    List<User> findAllWithRole();

    boolean existsByEmail(String email);

    boolean existsByPseudo(String pseudo);

    boolean existsByEmailAndUserIdNot(
            String email,
            String userId
    );

    boolean existsByPseudoAndUserIdNot(
            String pseudo,
            String userId
    );
}
