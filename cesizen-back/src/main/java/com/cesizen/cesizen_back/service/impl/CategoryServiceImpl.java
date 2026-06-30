package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.dto.user.CategoryRequest;
import com.cesizen.cesizen_back.dto.user.CategoryResponse;
import com.cesizen.cesizen_back.entity.Category;
import com.cesizen.cesizen_back.exception.BadRequestException;
import com.cesizen.cesizen_back.exception.NotFoundException;
import com.cesizen.cesizen_back.mapper.CategoryMapper;
import com.cesizen.cesizen_back.repository.CategoryRepository;
import com.cesizen.cesizen_back.service.CategoryService;
import com.cesizen.cesizen_back.repository.InformationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repo;
    private final CategoryMapper mapper;
    private final InformationRepository informationRepository;


    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest req) {

        if (req.getName() == null || req.getName().isBlank()) {
            throw new BadRequestException("Le nom de la catégorie est obligatoire.");
        }

        if (repo.existsByName(req.getName())) {
            throw new BadRequestException("Une catégorie avec ce nom existe déjà.");
        }

        Category c = new Category();
        c.setName(req.getName().trim());
        c.setDescription(req.getDescription());

        Category saved = repo.save(c);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CategoryResponse update(UUID id, CategoryRequest req) {

        Category c = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Catégorie introuvable."));

        if (req.getName() == null || req.getName().isBlank()) {
            throw new BadRequestException("Le nom de la catégorie est obligatoire.");
        }

        String newName = req.getName().trim();

        boolean nameExists = repo.existsByName(newName);
        boolean isDifferent = !c.getName().equals(newName);

        if (nameExists && isDifferent) {
            throw new BadRequestException("Une catégorie avec ce nom existe déjà.");
        }

        c.setName(newName);
        c.setDescription(req.getDescription());

        Category saved = repo.save(c);

        return mapper.toResponse(saved);
    }

@Override
@Transactional
public void delete(UUID id) {

    Category c = repo.findById(id)
            .orElseThrow(() -> new NotFoundException("Catégorie introuvable."));

    if (informationRepository.existsByCategory_CategoryId(id)) {
        throw new BadRequestException("Impossible de supprimer une catégorie utilisée par des informations.");
    }

    repo.delete(c);
}


    @Override
    @Transactional(readOnly = true)
    public CategoryResponse findById(UUID id) {

        Category c = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Catégorie introuvable."));

        return mapper.toResponse(c);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return repo.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
