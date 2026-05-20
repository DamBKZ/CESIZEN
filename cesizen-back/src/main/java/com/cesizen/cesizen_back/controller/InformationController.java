package com.cesizen.cesizen_back.controller;

import com.cesizen.cesizen_back.dto.user.InformationRequest;
import com.cesizen.cesizen_back.dto.user.InformationResponse;
import com.cesizen.cesizen_back.service.InformationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/information")
@RequiredArgsConstructor
public class InformationController {

    private final InformationService service;

    @GetMapping
    public Page<InformationResponse> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/search")
    public Page<InformationResponse> search(@RequestParam String keyword, Pageable pageable) {
        return service.search(keyword, pageable);
    }

    @GetMapping("/{id}")
    public InformationResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    public InformationResponse create(@Valid @RequestBody InformationRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public InformationResponse update(@PathVariable UUID id, @Valid @RequestBody InformationRequest request) {
        return service.update(id, request);
    }
}
