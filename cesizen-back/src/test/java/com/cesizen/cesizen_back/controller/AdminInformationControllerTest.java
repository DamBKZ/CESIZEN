package com.cesizen.cesizen_back.controller;

import com.cesizen.cesizen_back.dto.user.InformationRequest;
import com.cesizen.cesizen_back.dto.user.InformationResponse;
import com.cesizen.cesizen_back.entity.InformationType;
import com.cesizen.cesizen_back.service.InformationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminInformationControllerTest {

    private MockMvc mockMvc;
    private InformationService service;

    @BeforeEach
    void setup() {
        service = mock(InformationService.class);
        AdminInformationController controller = new AdminInformationController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // ------------------------------------------------------------
    // GET ALL
    // ------------------------------------------------------------
    @Test
    void findAll_shouldReturnPage() throws Exception {
        Page<InformationResponse> page = new PageImpl<>(List.of(
                InformationResponse.builder().title("Test").build()
        ));

        when(service.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/admin/information"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test"));
    }

    // ------------------------------------------------------------
    // FILTER
    // ------------------------------------------------------------
    @Test
    void filter_shouldReturnPage() throws Exception {
        Page<InformationResponse> page = new PageImpl<>(List.of(
                InformationResponse.builder().title("Filtered").build()
        ));

        when(service.filter(eq(InformationType.ARTICLE), any(), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/admin/information/filter")
                .param("type", "ARTICLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Filtered"));
    }

    // ------------------------------------------------------------
    // SEARCH
    // ------------------------------------------------------------
    @Test
    void search_shouldReturnPage() throws Exception {
        Page<InformationResponse> page = new PageImpl<>(List.of(
                InformationResponse.builder().title("Search").build()
        ));

        when(service.search(eq("abc"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/admin/information/search")
                .param("keyword", "abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Search"));
    }

    // ------------------------------------------------------------
    // FIND BY ID
    // ------------------------------------------------------------
    @Test
    void findById_shouldReturnResponse() throws Exception {
        UUID id = UUID.randomUUID();

        InformationResponse resp = InformationResponse.builder()
                .informationId(id)
                .title("Detail")
                .build();

        when(service.findById(id)).thenReturn(resp);

        mockMvc.perform(get("/api/admin/information/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Detail"));
    }

    // ------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------
    @Test
    void create_shouldReturn201() throws Exception {
        InformationResponse resp = InformationResponse.builder()
                .title("Created")
                .build();

        when(service.create(any(InformationRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/admin/information")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "type": "ARTICLE",
                          "title": "Created",
                          "author": "Me",
                          "slug": "slug",
                          "tags": ["t1"],
                          "categoryId": "00000000-0000-0000-0000-000000000001",
                          "content": "Hello"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Created"));
    }

    // ------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------
    @Test
    void update_shouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();

        InformationResponse resp = InformationResponse.builder()
                .title("Updated")
                .build();

        when(service.update(eq(id), any(InformationRequest.class))).thenReturn(resp);

        mockMvc.perform(put("/api/admin/information/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "type": "ARTICLE",
                          "title": "Updated",
                          "author": "Me",
                          "slug": "slug",
                          "tags": ["t1"],
                          "categoryId": "00000000-0000-0000-0000-000000000001",
                          "content": "Hello"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    // ------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------
    @Test
    void delete_shouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/admin/information/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Information supprimée."));

        verify(service).delete(id);
    }
}
