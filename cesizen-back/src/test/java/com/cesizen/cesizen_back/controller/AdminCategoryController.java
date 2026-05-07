package com.cesizen.cesizen_back.controller;

import com.cesizen.cesizen_back.dto.user.CategoryRequest;
import com.cesizen.cesizen_back.entity.Category;
import com.cesizen.cesizen_back.service.CategoryService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminCategoryController.class)
class AdminCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

        @MockitoBean
        private CategoryService categoryService;


    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAll_shouldReturnList() throws Exception {
        Category c = Category.builder().categoryId("1").name("Stress").build();
        Mockito.when(categoryService.findAll()).thenReturn(List.of(c));

        mockMvc.perform(get("/api/admin/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Stress"));
    }

    @Test
    void create_shouldReturn201() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("Stress");
        request.setDescription("Gestion du stress");

        Category saved = Category.builder()
                .categoryId("1")
                .name("Stress")
                .description("Gestion du stress")
                .build();

        Mockito.when(categoryService.create(any())).thenReturn(saved);

        mockMvc.perform(post("/api/admin/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Stress"));
    }

    @Test
    void update_shouldReturn200() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("Updated");
        request.setDescription("Updated desc");

        Category updated = Category.builder()
                .categoryId("1")
                .name("Updated")
                .description("Updated desc")
                .build();

        Mockito.when(categoryService.update(any(), any())).thenReturn(updated);

        mockMvc.perform(put("/api/admin/category/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void delete_shouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/admin/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Catégorie supprimée."));
    }
}
