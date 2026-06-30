package com.cesizen.cesizen_back.controller;

import com.cesizen.cesizen_back.dto.user.DiagnosticHistoryResponse;
import com.cesizen.cesizen_back.dto.user.DiagnosticResponse;
import com.cesizen.cesizen_back.dto.user.DiagnosticSubmitRequest;
import com.cesizen.cesizen_back.entity.User;
import com.cesizen.cesizen_back.service.DiagnosticService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnostic")
@RequiredArgsConstructor
public class DiagnosticController {

    private final DiagnosticService diagnosticService;

    @PostMapping("/submit")
    public ResponseEntity<DiagnosticResponse> submit(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody DiagnosticSubmitRequest request
    ) {
        return ResponseEntity.ok(diagnosticService.submit(request, currentUser));
    }

    @GetMapping("/history/me")
    public ResponseEntity<List<DiagnosticHistoryResponse>> history(
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(diagnosticService.history(currentUser));
    }
}
