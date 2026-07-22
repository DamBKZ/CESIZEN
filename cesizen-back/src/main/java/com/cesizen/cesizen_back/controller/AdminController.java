package com.cesizen.cesizen_back.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cesizen.cesizen_back.repository.DiagnosticSurveyRepository;
import com.cesizen.cesizen_back.repository.InformationRepository;
import com.cesizen.cesizen_back.repository.LogRepository;
import com.cesizen.cesizen_back.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final InformationRepository informationRepository;
    private final DiagnosticSurveyRepository diagnosticSurveyRepository;
    private final LogRepository logRepository;

    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("users", userRepository.count());
        stats.put("informations", informationRepository.count());
        stats.put("diagnostics", diagnosticSurveyRepository.count());
        stats.put("logs", logRepository.count());
        return stats;
    }
}

