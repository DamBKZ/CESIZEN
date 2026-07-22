package com.cesizen.cesizen_back.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(
            AccessDeniedException exception
    ) {
        log.warn("Accès refusé (403) : {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", safeMessage(
                        exception,
                        "Accès refusé."
                )));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleUnreadableJson(
            HttpMessageNotReadableException exception
    ) {
        log.warn("Requête JSON invalide (400) : {}", exception.getMessage());

        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "error",
                        "Requête JSON invalide ou valeur non reconnue."
                ));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(
            BadRequestException exception
    ) {
        log.warn("Erreur métier (400) : {}", exception.getMessage());

        return ResponseEntity
                .badRequest()
                .body(Map.of("error", safeMessage(
                        exception,
                        "Requête invalide."
                )));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(
            NotFoundException exception
    ) {
        log.warn("Ressource introuvable (404) : {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", safeMessage(
                        exception,
                        "Ressource introuvable."
                )));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(
            IllegalArgumentException exception
    ) {
        log.warn("Argument invalide (400) : {}", exception.getMessage());

        return ResponseEntity
                .badRequest()
                .body(Map.of("error", safeMessage(
                        exception,
                        "Requête invalide."
                )));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(
            IllegalStateException exception
    ) {
        log.warn("État incompatible (409) : {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("error", safeMessage(
                        exception,
                        "L'opération ne peut pas être effectuée dans l'état actuel."
                )));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Erreur de validation.");

        log.warn("Erreur de validation (400) : {}", message);

        return ResponseEntity
                .badRequest()
                .body(Map.of("error", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(
            Exception exception
    ) {
        log.error(
                "Erreur inattendue (500) : {}",
                exception.getMessage(),
                exception
        );

        return ResponseEntity
                .internalServerError()
                .body(Map.of(
                        "error",
                        "Une erreur interne est survenue."
                ));
    }

    private String safeMessage(
            Exception exception,
            String fallback
    ) {
        String message = exception.getMessage();

        return message == null || message.isBlank()
                ? fallback
                : message;
    }
}
