package com.cesizen.cesizen_back.controller;

import com.cesizen.cesizen_back.dto.user.InformationRequest;
import com.cesizen.cesizen_back.dto.user.InformationResponse;
import com.cesizen.cesizen_back.entity.InformationType;
import com.cesizen.cesizen_back.service.InformationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/information")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminInformationController {

    private final InformationService service;

    @GetMapping
    public ResponseEntity<Page<InformationResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<InformationResponse>> filter(
            @RequestParam(required = false) InformationType type,
            @RequestParam(required = false) UUID categoryId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.filter(type, categoryId, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<InformationResponse>> search(
            @RequestParam String keyword,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.search(keyword, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InformationResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<InformationResponse> create(@Valid @RequestBody InformationRequest request) {
        return ResponseEntity.status(201).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InformationResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody InformationRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of("message", "Information supprimée."));
    }
}

