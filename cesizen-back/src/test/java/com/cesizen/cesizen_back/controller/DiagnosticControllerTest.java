package com.cesizen.cesizen_back.controller;

import com.cesizen.cesizen_back.controller.DiagnosticController;
import com.cesizen.cesizen_back.dto.user.DiagnosticResponse;
import com.cesizen.cesizen_back.dto.user.DiagnosticSubmitRequest;
import com.cesizen.cesizen_back.service.DiagnosticService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DiagnosticController.class)
class DiagnosticControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DiagnosticService diagnosticService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void submit_shouldReturnDiagnosticResponse() throws Exception {
        DiagnosticResponse mockResponse = new DiagnosticResponse(
                "survey-123", 120, "MEDIUM", null
        );

        Mockito.when(diagnosticService.submit(any())).thenReturn(mockResponse);

        DiagnosticSubmitRequest request = new DiagnosticSubmitRequest();
        request.setUserId("user-123");
        request.setAnswers(Map.of("event-1", true));

        mockMvc.perform(post("/api/diagnostic/submit")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(120))
                .andExpect(jsonPath("$.riskLevel").value("MEDIUM"));
    }

    @Test
    void history_shouldReturnList() throws Exception {
        Mockito.when(diagnosticService.history("user-123"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/diagnostic/history/user-123"))
                .andExpect(status().isOk());
    }
}
