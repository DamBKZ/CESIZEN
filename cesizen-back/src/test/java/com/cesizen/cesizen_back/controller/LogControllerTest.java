package com.cesizen.cesizen_back.controller;

import com.cesizen.cesizen_back.entity.Log;
import com.cesizen.cesizen_back.service.LogService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LogController.class)
class LogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LogService logService;

    @Test
    void createLog_shouldReturnLog() throws Exception {
        Log log = Log.builder()
                .logId(1)
                .userId("user-123")
                .content("Test log")
                .build();

        Mockito.when(logService.createLog("user-123", "Test log"))
                .thenReturn(log);

        mockMvc.perform(post("/api/logs")
                        .param("userId", "user-123")
                        .param("content", "Test log"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logId").value(1))
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.content").value("Test log"));
    }

    @Test
    void getLogsForUser_shouldReturnList() throws Exception {
        Mockito.when(logService.getLogsForUser("user-123"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/logs/user/user-123"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllLogs_shouldReturnList() throws Exception {
        Mockito.when(logService.getAllLogs())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/logs"))
                .andExpect(status().isOk());
    }
}
