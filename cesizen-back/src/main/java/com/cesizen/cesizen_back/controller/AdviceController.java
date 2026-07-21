package com.cesizen.cesizen_back.controller;

import org.springframework.web.bind.annotation.*;

import com.cesizen.cesizen_back.service.AdviceService;

import java.util.List;

@RestController
@RequestMapping("/api/advice")
public class AdviceController {

    private final AdviceService service;

    public AdviceController(AdviceService service) {
        this.service = service;
    }

    @GetMapping("/{level}")
    public List<String> getAdviceByLevel(@PathVariable String level) {
        return service.getAdviceByLevel(level);
    }
}
