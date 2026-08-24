package com.cesizen.cesizen_back.security.jwt;

import com.cesizen.cesizen_back.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (!hasBearerToken(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        /*
         * Si un filtre précédent a déjà authentifié la requête,
         * il n'est pas nécessaire de traiter de nouveau le JWT.
         */
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();

        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (!jwtService.isTokenValid(token)) {
                log.debug(
                        "JWT invalide ou expiré pour {} {}",
                        request.getMethod(),
                        request.getRequestURI()
                );

                filterChain.doFilter(request, response);
                return;
            }

            String userId = jwtService.extractUserId(token);

            if (userId == null || userId.isBlank()) {
                log.debug("JWT sans identifiant utilisateur.");

                filterChain.doFilter(request, response);
                return;
            }

            var user = userRepository
                    .findByUserIdWithRole(userId)
                    .orElse(null);

            if (user == null) {
                log.warn(
                        "Utilisateur JWT introuvable pour userId={}",
                        userId
                );

                filterChain.doFilter(request, response);
                return;
            }

            if (!user.isEnabled()) {
                log.warn(
                        "Tentative d'utilisation d'un JWT par le compte désactivé {}",
                        userId
                );

                filterChain.doFilter(request, response);
                return;
            }

            var authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

        } catch (JwtException | IllegalArgumentException exception) {
            log.debug(
                    "JWT rejeté pour {} {} : {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    exception.getMessage()
            );

        } catch (Exception exception) {
            log.error(
                    "Erreur inattendue pendant l'authentification JWT.",
                    exception
            );
        }

        filterChain.doFilter(request, response);
    }

    private boolean hasBearerToken(String authHeader) {
        return authHeader != null
                && authHeader.startsWith(BEARER_PREFIX);
    }

@Override
protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();

    return path.equals("/auth")
            || path.startsWith("/auth/")
            || path.equals("/api/users/register")
            || path.equals("/actuator")
            || path.startsWith("/actuator/");
}
}
