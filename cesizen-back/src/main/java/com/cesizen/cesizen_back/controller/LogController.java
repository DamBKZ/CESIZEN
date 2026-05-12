package com.cesizen.cesizen_back.controller;

import com.cesizen.cesizen_back.entity.Log;
import com.cesizen.cesizen_back.service.LogService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @PostMapping
    public ResponseEntity<Log> createLog(
            @RequestParam(required = false) String userId,
            @RequestParam String content
    ) {
        Log log = logService.createLog(userId, content);
        return ResponseEntity.ok(log);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Log>> getLogsForUser(@PathVariable String userId) {
        return ResponseEntity.ok(logService.getLogsForUser(userId));
    }

    @GetMapping
    public ResponseEntity<List<Log>> getAllLogs() {
        return ResponseEntity.ok(logService.getAllLogs());
    }
}
