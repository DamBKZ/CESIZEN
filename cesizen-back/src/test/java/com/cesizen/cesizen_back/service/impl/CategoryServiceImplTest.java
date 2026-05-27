package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.dto.user.CategoryRequest;
import com.cesizen.cesizen_back.dto.user.CategoryResponse;
import com.cesizen.cesizen_back.entity.Category;
import com.cesizen.cesizen_back.mapper.categoryMapper;
import com.cesizen.cesizen_back.repository.CategoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    private CategoryRepository repo;
    private categoryMapper mapper;
    private CategoryServiceImpl service;

    @BeforeEach
    void setup() {
        repo = mock(CategoryRepository.class);
        mapper = mock(categoryMapper.class);
        service = new CategoryServiceImpl(repo, mapper);
    }

    @Test
    void create_shouldSaveAndReturnResponse() {
        CategoryRequest req = new CategoryRequest();
        req.setName("Sport");
        req.setDescription("Activités sportives");

        Category saved = Category.builder()
                .categoryId(UUID.randomUUID())
                .name("Sport")
                .description("Activités sportives")
                .createdAt(LocalDateTime.now())
                .build();

        when(repo.save(any(Category.class))).thenReturn(saved);

        CategoryResponse expected = CategoryResponse.builder()
                .categoryId(saved.getCategoryId())
                .name(saved.getName())
                .description(saved.getDescription())
                .createdAt(saved.getCreatedAt().toString())
                .build();

        when(mapper.toResponse(saved)).thenReturn(expected);

        CategoryResponse result = service.create(req);

        assertEquals("Sport", result.getName());
        verify(repo, times(1)).save(any(Category.class));
        verify(mapper).toResponse(saved);
    }

    @Test
    void update_shouldModifyAndReturnResponse() {
        UUID id = UUID.randomUUID();

        Category existing = Category.builder()
                .categoryId(id)
                .name("Old")
                .description("Old desc")
                .createdAt(LocalDateTime.now())
                .build();

        CategoryRequest req = new CategoryRequest();
        req.setName("New");
        req.setDescription("New desc");

        when(repo.findById(id)).thenReturn(Optional.of(existing));

        CategoryResponse expected = CategoryResponse.builder()
                .categoryId(id)
                .name("New")
                .description("New desc")
                .createdAt(existing.getCreatedAt().toString())
                .build();

        when(mapper.toResponse(existing)).thenReturn(expected);

        CategoryResponse result = service.update(id, req);

        verify(mapper).updateEntity(existing, req);
        verify(repo).save(existing);
        assertEquals("New", result.getName());
    }

    @Test
    void update_shouldThrowIfNotFound() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());

        CategoryRequest req = new CategoryRequest();
        req.setName("Test");

        assertThrows(RuntimeException.class, () -> service.update(id, req));
    }

    @Test
    void delete_shouldCallRepository() {
        UUID id = UUID.randomUUID();

        service.delete(id);

        verify(repo).deleteById(id);
    }

    @Test
    void findById_shouldReturnResponse() {
        UUID id = UUID.randomUUID();

        Category c = Category.builder()
                .categoryId(id)
                .name("Test")
                .description("Desc")
                .createdAt(LocalDateTime.now())
                .build();

        CategoryResponse expected = CategoryResponse.builder()
                .categoryId(id)
                .name("Test")
                .description("Desc")
                .createdAt(c.getCreatedAt().toString())
                .build();

        when(repo.findById(id)).thenReturn(Optional.of(c));
        when(mapper.toResponse(c)).thenReturn(expected);

        CategoryResponse result = service.findById(id);

        assertEquals("Test", result.getName());
    }

    @Test
    void findById_shouldThrowIfNotFound() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.findById(id));
    }

    @Test
    void findAll_shouldReturnMappedList() {
        Category c1 = Category.builder()
                .categoryId(UUID.randomUUID())
                .name("A")
                .description("D1")
                .createdAt(LocalDateTime.now())
                .build();

        Category c2 = Category.builder()
                .categoryId(UUID.randomUUID())
                .name("B")
                .description("D2")
                .createdAt(LocalDateTime.now())
                .build();

        when(repo.findAll()).thenReturn(List.of(c1, c2));

        when(mapper.toResponse(c1)).thenReturn(
                CategoryResponse.builder()
                        .categoryId(c1.getCategoryId())
                        .name("A")
                        .description("D1")
                        .createdAt(c1.getCreatedAt().toString())
                        .build()
        );

        when(mapper.toResponse(c2)).thenReturn(
                CategoryResponse.builder()
                        .categoryId(c2.getCategoryId())
                        .name("B")
                        .description("D2")
                        .createdAt(c2.getCreatedAt().toString())
                        .build()
        );

        List<CategoryResponse> result = service.findAll();

        assertEquals(2, result.size());
        verify(repo).findAll();
    }
}
