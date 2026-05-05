package com.cesizen.cesizen_back.controller;

import com.cesizen.cesizen_back.dto.user.AdminUserResponse;
import com.cesizen.cesizen_back.entity.User;
import com.cesizen.cesizen_back.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    // -------------------------------------------------------------------------
    // LISTE DES UTILISATEURS
    // -------------------------------------------------------------------------

    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> findAll() {
        List<AdminUserResponse> users = userService.findAll()
                .stream()
                .map(this::toAdminResponse)
                .toList();

        return ResponseEntity.ok(users);
    }

    // -------------------------------------------------------------------------
    // DÉTAIL D'UN UTILISATEUR
    // -------------------------------------------------------------------------

    @GetMapping("/{id}")
    public ResponseEntity<AdminUserResponse> findById(@PathVariable String id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(toAdminResponse(user));
    }

    // -------------------------------------------------------------------------
    // DÉSACTIVATION
    // -------------------------------------------------------------------------

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, String>> deactivate(@PathVariable String id) {
        userService.deactivate(id);
        return ResponseEntity.ok(Map.of("message", "Compte désactivé."));
    }

    // -------------------------------------------------------------------------
    // ACTIVATION
    // -------------------------------------------------------------------------

    @PutMapping("/{id}/activate")
    public ResponseEntity<Map<String, String>> activate(@PathVariable String id) {
        userService.activate(id);
        return ResponseEntity.ok(Map.of("message", "Compte activé."));
    }

    // -------------------------------------------------------------------------
    // SUPPRESSION
    // -------------------------------------------------------------------------

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Compte supprimé."));
    }

    // -------------------------------------------------------------------------
    // HELPER PRIVÉ
    // -------------------------------------------------------------------------

    private AdminUserResponse toAdminResponse(User user) {
        return AdminUserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .pseudo(user.getPseudo())
                .role(user.getRole().getRoleName())
                .active(user.isActive())
                .userCreatedAt(user.getUserCreatedAt().toString())
                .build();
    }
}