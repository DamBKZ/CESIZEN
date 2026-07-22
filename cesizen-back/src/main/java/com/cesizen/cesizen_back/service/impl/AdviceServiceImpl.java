package com.cesizen.cesizen_back.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cesizen.cesizen_back.exception.NotFoundException;

import com.cesizen.cesizen_back.dto.user.AdviceRequest;
import com.cesizen.cesizen_back.dto.user.AdviceResponse;
import com.cesizen.cesizen_back.entity.AdviceEntity;
import com.cesizen.cesizen_back.repository.AdviceRepository;
import com.cesizen.cesizen_back.service.AdviceService;

import java.util.List;
import java.util.UUID;

@Service
public class AdviceServiceImpl implements AdviceService {

    private final AdviceRepository repo;

    public AdviceServiceImpl(AdviceRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdviceResponse> findAll() {
        return repo.findAll().stream()
                .map(a -> new AdviceResponse(a.getId(), a.getLevel(), a.getMessage()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AdviceResponse findById(String id) {
AdviceEntity a = repo.findById(id)
        .orElseThrow(() -> new NotFoundException("Conseil introuvable."));

        return new AdviceResponse(a.getId(), a.getLevel(), a.getMessage());
    }

    @Override
    @Transactional
    public AdviceResponse create(AdviceRequest request) {
        AdviceEntity a = new AdviceEntity();
        a.setId(UUID.randomUUID().toString());
a.setLevel(request.level().toUpperCase());

        a.setMessage(request.message());
        repo.save(a);
        return new AdviceResponse(a.getId(), a.getLevel(), a.getMessage());
    }

    @Override
    @Transactional
    public AdviceResponse update(String id, AdviceRequest request) {
AdviceEntity a = repo.findById(id)
        .orElseThrow(() -> new NotFoundException("Conseil introuvable."));

a.setLevel(request.level().toUpperCase());

        a.setMessage(request.message());
        repo.save(a);
        return new AdviceResponse(a.getId(), a.getLevel(), a.getMessage());
    }

@Override
@Transactional
public void delete(String id) {
    AdviceEntity a = repo.findById(id)
            .orElseThrow(() -> new NotFoundException("Conseil introuvable."));

    repo.delete(a);
}


    @Override
    @Transactional(readOnly = true)
    public List<String> getAdviceByLevel(String level) {
        return repo.findByLevel(level.toUpperCase()).stream()
                .map(AdviceEntity::getMessage)
                .toList();
    }
}
