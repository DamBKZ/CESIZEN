package com.cesizen.cesizen_back.controller;

import com.cesizen.cesizen_back.entity.DiagnosticEvent;
import com.cesizen.cesizen_back.repository.DiagnosticEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnostic/events")
@RequiredArgsConstructor
public class DiagnosticEventController {

    private final DiagnosticEventRepository eventRepository;

    @GetMapping
    public List<DiagnosticEvent> getEvents() {
        return eventRepository.findAll();
    }
}
