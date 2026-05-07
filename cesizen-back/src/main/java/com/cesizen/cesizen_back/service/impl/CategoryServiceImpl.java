package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.entity.Category;
import com.cesizen.cesizen_back.repository.CategoryRepository;
import com.cesizen.cesizen_back.service.CategoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable."));
    }

    @Override
    public Category create(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category update(String id, Category updated) {

        Category existing = findById(id);

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());

        return categoryRepository.save(existing);
    }

    @Override
    public void delete(String id) {
        categoryRepository.deleteById(id);
    }
}
