package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.dto.user.InformationRequest;
import com.cesizen.cesizen_back.dto.user.InformationResponse;
import com.cesizen.cesizen_back.entity.*;
import com.cesizen.cesizen_back.mapper.informationMapper;
import com.cesizen.cesizen_back.repository.CategoryRepository;
import com.cesizen.cesizen_back.repository.InformationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InformationServiceImplTest {

    private InformationRepository repo;
    private CategoryRepository categoryRepo;
    private informationMapper mapper;
    private InformationServiceImpl service;

    @BeforeEach
    void setup() {
        repo = mock(InformationRepository.class);
        categoryRepo = mock(CategoryRepository.class);
        mapper = mock(informationMapper.class);
        service = new InformationServiceImpl(repo, categoryRepo, mapper);
    }

    // ------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------
    @Test
    void create_shouldCreateArticle() {
        UUID categoryId = UUID.randomUUID();

        Category category = Category.builder()
                .categoryId(categoryId)
                .name("Sport")
                .build();

        InformationRequest req = new InformationRequest();
        req.setType(InformationType.ARTICLE);
        req.setTitle("Titre");
        req.setAuthor("Auteur");
        req.setSlug("slug");
        req.setTags(List.of("tag1"));
        req.setCategoryId(categoryId);
        req.setContent("Contenu");

        InformationArticle article = new InformationArticle(
                req.getTitle(),
                req.getAuthor(),
                req.getSlug(),
                req.getTags(),
                category,
                req.getContent()
        );

        when(categoryRepo.findById(categoryId)).thenReturn(Optional.of(category));
        when(repo.save(any())).thenReturn(article);

        InformationResponse expected = InformationResponse.builder()
                .informationId(UUID.randomUUID())
                .title("Titre")
                .type("ARTICLE")
                .author("Auteur")
                .slug("slug")
                .tags(List.of("tag1"))
                .categoryId(categoryId)
                .categoryName("Sport")
                .content("Contenu")
                .build();

        when(mapper.toResponse(article)).thenReturn(expected);

        InformationResponse result = service.create(req);

        assertEquals("Titre", result.getTitle());
        verify(repo).save(any(Information.class));
        verify(mapper).toResponse(article);
    }

    @Test
    void create_shouldThrowIfCategoryNotFound() {
        UUID categoryId = UUID.randomUUID();

        InformationRequest req = new InformationRequest();
        req.setCategoryId(categoryId);

        when(categoryRepo.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.create(req));
    }

    // ------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------
    @Test
    void update_shouldUpdateInformation() {
        UUID id = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Category category = Category.builder()
                .categoryId(categoryId)
                .name("Sport")
                .build();

        InformationArticle info = new InformationArticle(
                "Old", "Old", "old", List.of("t"), category, "Old content"
        );

        InformationRequest req = new InformationRequest();
        req.setTitle("New");
        req.setAuthor("New");
        req.setSlug("new");
        req.setTags(List.of("tag"));
        req.setCategoryId(categoryId);
        req.setContent("New content");

        when(repo.findById(id)).thenReturn(Optional.of(info));
        when(categoryRepo.findById(categoryId)).thenReturn(Optional.of(category));

        InformationResponse expected = InformationResponse.builder()
                .informationId(id)
                .title("New")
                .type("ARTICLE")
                .author("New")
                .slug("new")
                .tags(List.of("tag"))
                .categoryId(categoryId)
                .categoryName("Sport")
                .content("New content")
                .build();

        when(mapper.toResponse(info)).thenReturn(expected);

        InformationResponse result = service.update(id, req);

        verify(mapper).updateEntity(info, req, category);
        verify(repo).save(info);
        assertEquals("New", result.getTitle());
    }

    @Test
    void update_shouldThrowIfInformationNotFound() {
        UUID id = UUID.randomUUID();
        InformationRequest req = new InformationRequest();

        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.update(id, req));
    }

    @Test
    void update_shouldThrowIfCategoryNotFound() {
        UUID id = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        InformationRequest req = new InformationRequest();
        req.setCategoryId(categoryId);

        when(repo.findById(id)).thenReturn(Optional.of(mock(Information.class)));
        when(categoryRepo.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.update(id, req));
    }

    // ------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------
    @Test
    void delete_shouldCallRepository() {
        UUID id = UUID.randomUUID();

        service.delete(id);

        verify(repo).deleteById(id);
    }

    // ------------------------------------------------------------
    // FIND BY ID
    // ------------------------------------------------------------
    @Test
    void findById_shouldReturnResponse() {
        UUID id = UUID.randomUUID();

        Information info = mock(Information.class);
        InformationResponse expected = mock(InformationResponse.class);

        when(repo.findById(id)).thenReturn(Optional.of(info));
        when(mapper.toResponse(info)).thenReturn(expected);

        InformationResponse result = service.findById(id);

        assertEquals(expected, result);
    }

    @Test
    void findById_shouldThrowIfNotFound() {
        UUID id = UUID.randomUUID();

        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.findById(id));
    }

    // ------------------------------------------------------------
    // FIND ALL
    // ------------------------------------------------------------
    @Test
    void findAll_shouldReturnMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Information info = mock(Information.class);
        InformationResponse resp = mock(InformationResponse.class);

        Page<Information> page = new PageImpl<>(List.of(info));

        when(repo.findAll(pageable)).thenReturn(page);
        when(mapper.toResponse(info)).thenReturn(resp);

        Page<InformationResponse> result = service.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        verify(mapper).toResponse(info);
    }

    // ------------------------------------------------------------
    // FILTER
    // ------------------------------------------------------------
    @Test
    void filter_shouldReturnMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Information info = mock(Information.class);
        InformationResponse resp = mock(InformationResponse.class);

        Page<Information> page = new PageImpl<>(List.of(info));

        when(repo.filter(InformationType.ARTICLE, null, pageable)).thenReturn(page);
        when(mapper.toResponse(info)).thenReturn(resp);

        Page<InformationResponse> result = service.filter(InformationType.ARTICLE, null, pageable);

        assertEquals(1, result.getTotalElements());
        verify(mapper).toResponse(info);
    }

    // ------------------------------------------------------------
    // SEARCH
    // ------------------------------------------------------------
    @Test
    void search_shouldReturnMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Information info = mock(Information.class);
        InformationResponse resp = mock(InformationResponse.class);

        Page<Information> page = new PageImpl<>(List.of(info));

        when(repo.search("test", pageable)).thenReturn(page);
        when(mapper.toResponse(info)).thenReturn(resp);

        Page<InformationResponse> result = service.search("test", pageable);

        assertEquals(1, result.getTotalElements());
        verify(mapper).toResponse(info);
    }
}
