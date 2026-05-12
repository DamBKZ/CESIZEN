package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.entity.Log;
import com.cesizen.cesizen_back.repository.LogRepository;
import com.cesizen.cesizen_back.service.LogService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;

    @Override
    public Log createLog(String userId, String content) {
        Log log = Log.builder()
                .userId(userId)
                .content(content)
                .build();

        return logRepository.save(log);
    }

    @Override
    public List<Log> getLogsForUser(String userId) {
        return logRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<Log> getAllLogs() {
        return logRepository.findAll();
    }
}
