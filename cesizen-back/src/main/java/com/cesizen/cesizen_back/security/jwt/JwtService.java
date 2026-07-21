package com.cesizen.cesizen_back.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    private static final int MINIMUM_SECRET_LENGTH_BYTES = 32;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms:3600000}")
    private long expirationMs;

    private SecretKey signingKey;

    @PostConstruct
    void initializeSigningKey() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "La variable JWT_SECRET est obligatoire."
            );
        }

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < MINIMUM_SECRET_LENGTH_BYTES) {
            throw new IllegalStateException(
                    "JWT_SECRET doit contenir au moins 32 octets."
            );
        }

        if (expirationMs <= 0) {
            throw new IllegalStateException(
                    "jwt.expiration-ms doit être supérieur à zéro."
            );
        }

        signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(
            String userId,
            String roleName
    ) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException(
                    "L'identifiant utilisateur est obligatoire."
            );
        }

        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException(
                    "Le rôle utilisateur est obligatoire."
            );
        }

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .claims(Map.of(
                        "userId", userId,
                        "role", roleName
                ))
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMs))
                .signWith(signingKey)
                .compact();
    }

    public String extractUserId(String token) {
        return extractClaim(
                token,
                claims -> claims.get("userId", String.class)
        );
    }

    public String extractRole(String token) {
        return extractClaim(
                token,
                claims -> claims.get("role", String.class)
        );
    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> resolver
    ) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "Le token JWT est obligatoire."
            );
        }

        return resolver.apply(extractAllClaims(token));
    }

    public boolean isTokenValid(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        try {
            Claims claims = extractAllClaims(token);

            String userId = claims.get("userId", String.class);
            String role = claims.get("role", String.class);

            return userId != null
                    && !userId.isBlank()
                    && role != null
                    && !role.isBlank();

        } catch (JwtException | IllegalArgumentException exception) {
            log.debug(
                    "Token JWT invalide : {}",
                    exception.getMessage()
            );
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
