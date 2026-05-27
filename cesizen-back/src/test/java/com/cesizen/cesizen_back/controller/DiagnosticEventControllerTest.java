package com.cesizen.cesizen_back.controller;

import com.cesizen.cesizen_back.dto.user.DiagnosticHistoryResponse;
import com.cesizen.cesizen_back.dto.user.DiagnosticResponse;
import com.cesizen.cesizen_back.dto.user.DiagnosticSubmitRequest;
import com.cesizen.cesizen_back.entity.DiagnosticEvent;
import com.cesizen.cesizen_back.repository.DiagnosticEventRepository;
import com.cesizen.cesizen_back.service.DiagnosticService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DiagnosticEventController.class)
class DiagnosticEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DiagnosticEventRepository eventRepository;

    @MockitoBean
    private DiagnosticService diagnosticService;

    private DiagnosticEvent e1;
    private DiagnosticEvent e2;

    @BeforeEach
    void setup() {
        e1 = DiagnosticEvent.builder()
                .eventId("E1")
                .label("Event 1")
                .lcu(50)
                .build();

        e2 = DiagnosticEvent.builder()
                .eventId("E2")
                .label("Event 2")
                .lcu(100)
                .build();
    }

    @Test
    void getEvents_shouldReturnListOfEvents() throws Exception {

        when(eventRepository.findAll()).thenReturn(List.of(e1, e2));

        mockMvc.perform(get("/api/diagnostic/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId").value("E1"))
                .andExpect(jsonPath("$[0].label").value("Event 1"))
                .andExpect(jsonPath("$[0].lcu").value(50))
                .andExpect(jsonPath("$[1].eventId").value("E2"))
                .andExpect(jsonPath("$[1].label").value("Event 2"))
                .andExpect(jsonPath("$[1].lcu").value(100));
    }

    @Test
    void submit_shouldReturnDiagnosticResponse() throws Exception {

        DiagnosticResponse response = DiagnosticResponse.builder()
                .surveyId("SURV123")
                .score(150)
                .riskLevel("MEDIUM")
                .createdAt(LocalDateTime.now().toString())
                .build();

        when(diagnosticService.submit(any(DiagnosticSubmitRequest.class)))
                .thenReturn(response);

        String json = """
                {
                    "userId": "USER123",
                    "answers": {
                        "E1": true,
                        "E2": false
                    }
                }
                """;

        mockMvc.perform(post("/api/diagnostic/submit")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.surveyId").value("SURV123"))
                .andExpect(jsonPath("$.score").value(150))
                .andExpect(jsonPath("$.riskLevel").value("MEDIUM"))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(diagnosticService).submit(any(DiagnosticSubmitRequest.class));
    }

    @Test
    void history_shouldReturnUserHistory() throws Exception {

        DiagnosticHistoryResponse h1 = DiagnosticHistoryResponse.builder()
                .surveyId("S1")
                .score(100)
                .riskLevel("LOW")
                .createdAt(LocalDateTime.now().toString())
                .build();

        DiagnosticHistoryResponse h2 = DiagnosticHistoryResponse.builder()
                .surveyId("S2")
                .score(300)
                .riskLevel("HIGH")
                .createdAt(LocalDateTime.now().toString())
                .build();

        when(diagnosticService.history("USER123"))
                .thenReturn(List.of(h1, h2));

        mockMvc.perform(get("/api/diagnostic/history/USER123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].surveyId").value("S1"))
                .andExpect(jsonPath("$[0].score").value(100))
                .andExpect(jsonPath("$[0].riskLevel").value("LOW"))
                .andExpect(jsonPath("$[1].surveyId").value("S2"))
                .andExpect(jsonPath("$[1].score").value(300))
                .andExpect(jsonPath("$[1].riskLevel").value("HIGH"));

        verify(diagnosticService).history("USER123");
    }
}
