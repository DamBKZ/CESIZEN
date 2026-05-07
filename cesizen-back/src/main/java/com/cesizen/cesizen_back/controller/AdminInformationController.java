package com.cesizen.cesizen_back.controller;

import com.cesizen.cesizen_back.dto.user.InformationRequest;
import com.cesizen.cesizen_back.dto.user.InformationResponse;
import com.cesizen.cesizen_back.entity.*;
import com.cesizen.cesizen_back.factory.InformationFactory;
import com.cesizen.cesizen_back.repository.CategoryRepository;
import com.cesizen.cesizen_back.service.InformationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/information")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminInformationController {

    private final InformationService informationService;
    private final CategoryRepository categoryRepository;

    // -----------------------------
    // PAGINATION + TRI
    // -----------------------------
    @GetMapping
    public ResponseEntity<Page<InformationResponse>> findAll(Pageable pageable) {
        Page<InformationResponse> page = informationService.findAll(pageable)
                .map(this::toResponse);

        return ResponseEntity.ok(page);
    }

    // -----------------------------
    // FILTRE TYPE + CATEGORIE
    // -----------------------------
    @GetMapping("/filter")
    public ResponseEntity<Page<InformationResponse>> filter(
            @RequestParam InformationType type,
            @RequestParam String categoryId,
            Pageable pageable
    ) {
        Page<InformationResponse> page = informationService
                .filter(type, categoryId, pageable)
                .map(this::toResponse);

        return ResponseEntity.ok(page);
    }

    // -----------------------------
    // RECHERCHE PAR MOT-CLE
    // -----------------------------
    @GetMapping("/search")
    public ResponseEntity<Page<InformationResponse>> search(
            @RequestParam String keyword,
            Pageable pageable
    ) {
        Page<InformationResponse> page = informationService
                .search(keyword, pageable)
                .map(this::toResponse);

        return ResponseEntity.ok(page);
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @GetMapping("/{id}")
    public ResponseEntity<InformationResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(toResponse(informationService.findById(id)));
    }

    // -----------------------------
    // CREATE
    // -----------------------------
    @PostMapping
    public ResponseEntity<InformationResponse> create(@Valid @RequestBody InformationRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable."));

        Information info = InformationFactory.create(
                request.getType(),
                request.getTitle(),
                request.getContent(),
                request.getVideoUrl(),
                request.getPdfUrl(),
                category
        );

        info.setAuthor(request.getAuthor());
        info.setTags(request.getTags());
        info.setSlug(request.getSlug());

        info = informationService.create(info);

        return ResponseEntity.status(201).body(toResponse(info));
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @PutMapping("/{id}")
    public ResponseEntity<InformationResponse> update(
            @PathVariable String id,
            @Valid @RequestBody InformationRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable."));

        Information updated = InformationFactory.create(
                request.getType(),
                request.getTitle(),
                request.getContent(),
                request.getVideoUrl(),
                request.getPdfUrl(),
                category
        );

        updated.setAuthor(request.getAuthor());
        updated.setTags(request.getTags());
        updated.setSlug(request.getSlug());

        Information info = informationService.update(id, updated);

        return ResponseEntity.ok(toResponse(info));
    }

    // -----------------------------
    // DELETE
    // -----------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id) {
        informationService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Information supprimée."));
    }

    // -----------------------------
    // MAPPING ENTITY -> DTO
    // -----------------------------
    private InformationResponse toResponse(Information info) {
        return InformationResponse.builder()
                .informationId(info.getInformationId())
                .title(info.getTitle())
                .type(info.getType().name())
                .categoryId(info.getCategory().getCategoryId())
                .categoryName(info.getCategory().getName())
                .createdAt(info.getCreatedAt().toString())
                .content(info instanceof InformationArticle a ? a.getContent() : null)
                .videoUrl(info instanceof InformationVideo v ? v.getVideoUrl() : null)
                .pdfUrl(info instanceof InformationPdf p ? p.getPdfUrl() : null)
                .author(info.getAuthor())
                .tags(info.getTags())
                .slug(info.getSlug())
                .build();
    }
}
