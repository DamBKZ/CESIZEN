package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.entity.Log;
import com.cesizen.cesizen_back.repository.LogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
@Disabled("Tests unitaires avec mocks désactivés : remplacement prévu par tests d'intégration")

class LogServiceImplTest {

    private LogRepository logRepository;
    private LogServiceImpl logService;

    @BeforeEach
    void setup() {
        logRepository = mock(LogRepository.class);
        logService = new LogServiceImpl(logRepository);
    }

    @Test
    void createLog_shouldSaveLog() {
        Log log = Log.builder()
                .logId(1)
                .userId("user-123")
                .content("Test log")
                .build();

        when(logRepository.save(any(Log.class))).thenReturn(log);

        Log result = logService.createLog("user-123", "Test log");

        assertThat(result.getUserId()).isEqualTo("user-123");
        assertThat(result.getContent()).isEqualTo("Test log");

        verify(logRepository).save(any(Log.class));
    }

    @Test
    void getLogsForUser_shouldReturnList() {
        when(logRepository.findByUserIdOrderByCreatedAtDesc("user-123"))
                .thenReturn(List.of());

        List<Log> result = logService.getLogsForUser("user-123");

        assertThat(result).isEmpty();
    }

    @Test
    void getAllLogs_shouldReturnList() {
        when(logRepository.findAll()).thenReturn(List.of());

        List<Log> result = logService.getAllLogs();

        assertThat(result).isEmpty();
    }
}
