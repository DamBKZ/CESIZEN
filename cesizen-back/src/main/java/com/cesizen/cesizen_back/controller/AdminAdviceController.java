package com.cesizen.cesizen_back.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.cesizen.cesizen_back.dto.user.AdviceRequest;
import com.cesizen.cesizen_back.dto.user.AdviceResponse;
import com.cesizen.cesizen_back.service.AdviceService;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/admin/advice")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAdviceController {

    private final AdviceService service;

    public AdminAdviceController(AdviceService service) {
        this.service = service;
    }

    @GetMapping
    public List<AdviceResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public AdviceResponse findById(@PathVariable String id) {
        return service.findById(id);
    }

@PostMapping
public AdviceResponse create(@Valid @RequestBody AdviceRequest request) {
    return service.create(request);
}


@PutMapping("/{id}")
public AdviceResponse update(
        @PathVariable String id,
        @Valid @RequestBody AdviceRequest request
) {
    return service.update(id, request);
}


    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
