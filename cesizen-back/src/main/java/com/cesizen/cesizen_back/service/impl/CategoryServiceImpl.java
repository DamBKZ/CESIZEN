package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.dto.user.CategoryRequest;
import com.cesizen.cesizen_back.dto.user.CategoryResponse;
import com.cesizen.cesizen_back.entity.Category;
import com.cesizen.cesizen_back.mapper.categoryMapper;
import com.cesizen.cesizen_back.repository.CategoryRepository;
import com.cesizen.cesizen_back.service.CategoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repo;
    private final categoryMapper mapper;

    @Override
    public CategoryResponse create(CategoryRequest req) {
        Category c = Category.builder()
                .name(req.getName())
                .description(req.getDescription())
                .build();

        repo.save(c);
        return mapper.toResponse(c);
    }

    @Override
    public CategoryResponse update(UUID id, CategoryRequest req) {
        Category c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

        mapper.updateEntity(c, req);
        repo.save(c);

        return mapper.toResponse(c);
    }

    @Override
    public void delete(UUID id) {
        repo.deleteById(id);
    }

    @Override
    public CategoryResponse findById(UUID id) {
        return repo.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));
    }

    @Override
    public List<CategoryResponse> findAll() {
        return repo.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }
}

