package com.cesizen.cesizen_back.controller;

import com.cesizen.cesizen_back.dto.user.CategoryRequest;
import com.cesizen.cesizen_back.dto.user.CategoryResponse;
import com.cesizen.cesizen_back.service.CategoryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminCategoryControllerTest {

    private MockMvc mockMvc;
    private CategoryService service;

    @BeforeEach
    void setup() {
        service = mock(CategoryService.class);
        AdminCategoryController controller = new AdminCategoryController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void create_shouldReturn201() throws Exception {
        CategoryResponse resp = CategoryResponse.builder()
                .categoryId(UUID.randomUUID())
                .name("Sport")
                .description("Desc")
                .createdAt("2024-01-01T10:00:00")
                .build();

        when(service.create(any(CategoryRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/admin/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "Sport",
                          "description": "Desc"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Sport"));
    }

    @Test
    void update_shouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();

        CategoryResponse resp = CategoryResponse.builder()
                .categoryId(id)
                .name("Updated")
                .description("New desc")
                .createdAt("2024-01-01T10:00:00")
                .build();

        when(service.update(eq(id), any(CategoryRequest.class))).thenReturn(resp);

        mockMvc.perform(put("/api/admin/category/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "Updated",
                          "description": "New desc"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void delete_shouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/admin/category/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Catégorie supprimée."));

        verify(service).delete(id);
    }
}
