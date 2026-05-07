package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.entity.Category;
import com.cesizen.cesizen_back.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_shouldReturnList() {
        Category c = Category.builder().categoryId("1").name("Stress").build();
        when(categoryRepository.findAll()).thenReturn(List.of(c));

        List<Category> result = categoryService.findAll();

        assertEquals(1, result.size());
        verify(categoryRepository).findAll();
    }

    @Test
    void findById_shouldReturnCategory() {
        Category c = Category.builder().categoryId("1").name("Stress").build();
        when(categoryRepository.findById("1")).thenReturn(Optional.of(c));

        Category result = categoryService.findById("1");

        assertEquals("1", result.getCategoryId());
    }

    @Test
    void findById_shouldThrowException() {
        when(categoryRepository.findById("404")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> categoryService.findById("404"));
    }

    @Test
    void create_shouldSaveCategory() {
        Category c = Category.builder().name("Stress").build();
        when(categoryRepository.save(c)).thenReturn(c);

        Category result = categoryService.create(c);

        assertEquals(c, result);
        verify(categoryRepository).save(c);
    }

    @Test
    void update_shouldModifyFields() {
        Category existing = Category.builder().categoryId("1").name("Old").description("Old desc").build();
        Category updated = Category.builder().name("New").description("New desc").build();

        when(categoryRepository.findById("1")).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(existing);

        Category result = categoryService.update("1", updated);

        assertEquals("New", result.getName());
        assertEquals("New desc", result.getDescription());
    }

    @Test
    void delete_shouldCallRepository() {
        categoryService.delete("1");
        verify(categoryRepository).deleteById("1");
    }
}
