package com.cesizen.cesizen_back.controller;

import com.cesizen.cesizen_back.dto.user.*;
import com.cesizen.cesizen_back.service.DiagnosticService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnostic")
@RequiredArgsConstructor
public class DiagnosticController {

    private final DiagnosticService diagnosticService;

    @PostMapping("/submit")
    public ResponseEntity<DiagnosticResponse> submit(@RequestBody DiagnosticSubmitRequest request) {
        return ResponseEntity.ok(diagnosticService.submit(request));
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<DiagnosticHistoryResponse>> history(@PathVariable String userId) {
        return ResponseEntity.ok(diagnosticService.history(userId));
    }
}
