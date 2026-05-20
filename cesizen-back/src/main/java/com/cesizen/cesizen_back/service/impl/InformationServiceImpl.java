package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.dto.user.InformationRequest;
import com.cesizen.cesizen_back.dto.user.InformationResponse;
import com.cesizen.cesizen_back.entity.Category;
import com.cesizen.cesizen_back.entity.Information;
import com.cesizen.cesizen_back.entity.InformationType;
import com.cesizen.cesizen_back.factory.InformationFactory;
import com.cesizen.cesizen_back.mapper.informationMapper;
import com.cesizen.cesizen_back.repository.CategoryRepository;
import com.cesizen.cesizen_back.repository.InformationRepository;
import com.cesizen.cesizen_back.service.InformationService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InformationServiceImpl implements InformationService {

    private final InformationRepository repo;
    private final CategoryRepository categoryRepo;
    private final informationMapper mapper;

    @Override
    public InformationResponse create(InformationRequest req) {
        Category category = categoryRepo.findById(req.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

        Information info = InformationFactory.create(req, category);
        repo.save(info);

        return mapper.toResponse(info);
    }

    @Override
    public InformationResponse update(UUID id, InformationRequest req) {
        Information info = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Information introuvable"));

        Category category = categoryRepo.findById(req.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

        mapper.updateEntity(info, req, category);
        repo.save(info);

        return mapper.toResponse(info);
    }

    @Override
    public void delete(UUID id) {
        repo.deleteById(id);
    }

    @Override
    public InformationResponse findById(UUID id) {
        return repo.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Information introuvable"));
    }

    @Override
    public Page<InformationResponse> findAll(Pageable pageable) {
        return repo.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    public Page<InformationResponse> filter(InformationType type, UUID categoryId, Pageable pageable) {
        return repo.filter(type, categoryId, pageable).map(mapper::toResponse);
    }

    @Override
    public Page<InformationResponse> search(String keyword, Pageable pageable) {
        return repo.search(keyword, pageable).map(mapper::toResponse);
    }
}

