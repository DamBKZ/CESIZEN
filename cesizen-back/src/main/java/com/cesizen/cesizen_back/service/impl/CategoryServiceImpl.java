package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.dto.user.CategoryRequest;
import com.cesizen.cesizen_back.dto.user.CategoryResponse;
import com.cesizen.cesizen_back.entity.Category;
import com.cesizen.cesizen_back.exception.BadRequestException;
import com.cesizen.cesizen_back.exception.NotFoundException;
import com.cesizen.cesizen_back.mapper.CategoryMapper;
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
    private final CategoryMapper mapper;
    @Override
    public CategoryResponse create(CategoryRequest req) {

        if (req.getName() == null || req.getName().isBlank()) {
            throw new BadRequestException("Le nom de la catégorie est obligatoire.");
        }

        if (repo.existsByName(req.getName())) {
            throw new BadRequestException("Une catégorie avec ce nom existe déjà.");
        }

        Category c = new Category();
        c.setName(req.getName());
        c.setDescription(req.getDescription());

        Category saved = repo.save(c);
        return mapper.toResponse(saved);
    }
    @Override
    public CategoryResponse update(UUID id, CategoryRequest req) {

        Category c = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Catégorie introuvable."));

        if (req.getName() == null || req.getName().isBlank()) {
            throw new BadRequestException("Le nom de la catégorie est obligatoire.");
        }

        boolean nameExists = repo.existsByName(req.getName());
        boolean isDifferent = !c.getName().equals(req.getName());

        if (nameExists && isDifferent) {
            throw new BadRequestException("Une catégorie avec ce nom existe déjà.");
        }

        mapper.updateEntity(c, req);
        repo.save(c);

        return mapper.toResponse(c);
    }
    @Override
    public void delete(UUID id) {

        Category c = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Catégorie introuvable."));

        repo.delete(c);
    }
    @Override
    public CategoryResponse findById(UUID id) {

        Category c = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Catégorie introuvable."));

        return mapper.toResponse(c);
    }
    @Override
    public List<CategoryResponse> findAll() {
        return repo.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
