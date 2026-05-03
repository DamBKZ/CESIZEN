package com.cesizen.cesizen_back.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // -------------------------------------------------------------------------
    // ERREURS MÉTIER
    // -------------------------------------------------------------------------

    /**
     * Données invalides ou ressource introuvable (400)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Erreur métier (400) : {}", e.getMessage());
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    /**
     * État incohérent — compte désactivé, token révoqué, etc. (403)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException e) {
        log.warn("Erreur d'état (403) : {}", e.getMessage());
        return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
    }

    // -------------------------------------------------------------------------
    // ERREURS DE VALIDATION (@Valid)
    // -------------------------------------------------------------------------

    /**
     * Champs invalides dans le body (400) — retourne le premier message d'erreur
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Erreur de validation.");
        log.warn("Erreur de validation (400) : {}", message);
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    // -------------------------------------------------------------------------
    // ERREUR GÉNÉRIQUE
    // -------------------------------------------------------------------------

    /**
     * Toute autre exception non gérée (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception e) {
        log.error("Erreur inattendue (500) : {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().body(Map.of("error", "Une erreur interne est survenue."));
    }
}