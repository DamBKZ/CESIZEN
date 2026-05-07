package com.cesizen.cesizen_back.controller;

import com.cesizen.cesizen_back.dto.user.InformationRequest;
import com.cesizen.cesizen_back.entity.*;
import com.cesizen.cesizen_back.service.InformationService;
import com.cesizen.cesizen_back.repository.CategoryRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminInformationController.class)
class AdminInformationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InformationService informationService;

    @MockitoBean
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_shouldReturn201() throws Exception {

        Category category = Category.builder()
                .categoryId("cat1")
                .name("Stress")
                .build();

        InformationArticle info = new InformationArticle();
        info.setInformationId("info1");
        info.setTitle("Titre");
        info.setContent("Contenu");
        info.setCategory(category);

        InformationRequest request = new InformationRequest();
        request.setTitle("Titre");
        request.setType(InformationType.ARTICLE);
        request.setCategoryId("cat1");
        request.setContent("Contenu");
        request.setAuthor("Damien");
        request.setSlug("titre-article");

        Mockito.when(categoryRepository.findById("cat1")).thenReturn(Optional.of(category));
        Mockito.when(informationService.create(any())).thenReturn(info);

        mockMvc.perform(post("/api/admin/information")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Titre"))
                .andExpect(jsonPath("$.content").value("Contenu"));
    }
}
