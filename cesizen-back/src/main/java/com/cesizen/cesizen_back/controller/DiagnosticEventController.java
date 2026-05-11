package com.cesizen.cesizen_back.controller;

import com.cesizen.cesizen_back.dto.user.DiagnosticSubmitRequest;
import com.cesizen.cesizen_back.dto.user.DiagnosticResponse;
import com.cesizen.cesizen_back.dto.user.DiagnosticHistoryResponse;
import com.cesizen.cesizen_back.entity.DiagnosticEvent;
import com.cesizen.cesizen_back.repository.DiagnosticEventRepository;
import com.cesizen.cesizen_back.service.DiagnosticService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnostic")
@RequiredArgsConstructor
public class DiagnosticEventController {

    private final DiagnosticEventRepository eventRepository;
    private final DiagnosticService diagnosticService;

    // GET /api/diagnostic/events
    @GetMapping("/events")
    public List<DiagnosticEvent> getEvents() {
        return eventRepository.findAll();
    }

    // POST /api/diagnostic/submit
    @PostMapping("/submit")
    public DiagnosticResponse submit(@RequestBody DiagnosticSubmitRequest request) {
        return diagnosticService.submit(request);
    }

    // GET /api/diagnostic/history/{userId}
    @GetMapping("/history/{userId}")
    public List<DiagnosticHistoryResponse> history(@PathVariable String userId) {
        return diagnosticService.history(userId);
    }
}
