package com.cesizen.cesizen_back.security.jwt;

import com.cesizen.cesizen_back.repository.UserRepository;
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

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Pas de token — on laisse passer, Spring Security gérera les accès non autorisés
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            if (!jwtService.isTokenValid(token)) {
                log.warn("Token JWT invalide ou expiré.");
                filterChain.doFilter(request, response);
                return;
            }

            // Ne pas re-authentifier si déjà authentifié dans ce contexte
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            String userId = jwtService.extractUserId(token);

            var user = userRepository.findById(userId).orElse(null);

            if (user == null) {
                log.warn("Utilisateur introuvable pour userId={}", userId);
                filterChain.doFilter(request, response);
                return;
            }

            // On utilise les authorities directement depuis l'entité User (UserDetails)
            // plutôt que de reconstruire depuis le token — plus sûr et cohérent
            var authToken = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities()
            );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (Exception e) {
            log.error("Erreur dans JwtAuthFilter : {}", e.getMessage());
            // On ne bloque pas la chaîne — Spring Security refusera l'accès si non authentifié
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Les routes /auth/** sont publiques — inutile de traiter le filtre JWT dessus.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().startsWith("/auth");
    }
}