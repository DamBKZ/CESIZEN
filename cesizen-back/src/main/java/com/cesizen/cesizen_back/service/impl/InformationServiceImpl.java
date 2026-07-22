package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.dto.user.InformationRequest;
import com.cesizen.cesizen_back.dto.user.InformationResponse;
import com.cesizen.cesizen_back.entity.Category;
import com.cesizen.cesizen_back.entity.Information;
import com.cesizen.cesizen_back.entity.InformationType;
import com.cesizen.cesizen_back.entity.User;
import com.cesizen.cesizen_back.exception.BadRequestException;
import com.cesizen.cesizen_back.exception.NotFoundException;
import com.cesizen.cesizen_back.factory.InformationFactory;
import com.cesizen.cesizen_back.mapper.InformationMapper;
import com.cesizen.cesizen_back.repository.CategoryRepository;
import com.cesizen.cesizen_back.repository.InformationRepository;
import com.cesizen.cesizen_back.service.InformationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InformationServiceImpl implements InformationService {

    private final InformationRepository repo;
    private final CategoryRepository categoryRepo;
    private final InformationMapper mapper;

    private void validateInformationRequest(InformationRequest req) {
        if (req == null) {
            throw new BadRequestException(
                    "Les données de l'information sont obligatoires."
            );
        }

        if (req.getType() == null) {
            throw new BadRequestException(
                    "Le type d'information est obligatoire."
            );
        }

        if (req.getTitle() == null || req.getTitle().isBlank()) {
            throw new BadRequestException(
                    "Le titre est obligatoire."
            );
        }

        if (req.getSlug() == null || req.getSlug().isBlank()) {
            throw new BadRequestException(
                    "Le slug est obligatoire."
            );
        }

        if (req.getCategoryId() == null) {
            throw new BadRequestException(
                    "La catégorie est obligatoire."
            );
        }

        switch (req.getType()) {
            case ARTICLE -> {
                if (req.getContent() == null || req.getContent().isBlank()) {
                    throw new BadRequestException(
                            "Le contenu est obligatoire pour un article."
                    );
                }
            }

            case VIDEO -> {
                if (req.getVideoUrl() == null || req.getVideoUrl().isBlank()) {
                    throw new BadRequestException(
                            "L'URL vidéo est obligatoire pour une vidéo."
                    );
                }
            }

            case PDF -> {
                if (req.getPdfUrl() == null || req.getPdfUrl().isBlank()) {
                    throw new BadRequestException(
                            "L'URL PDF est obligatoire pour un PDF."
                    );
                }
            }
        }
    }

    @Override
    @Transactional
    public InformationResponse create(
            InformationRequest req,
            User currentUser
    ) {
        validateInformationRequest(req);

        if (currentUser == null) {
            throw new AccessDeniedException(
                    "Utilisateur non authentifié."
            );
        }

        Category category = categoryRepo.findById(req.getCategoryId())
                .orElseThrow(() ->
                        new NotFoundException("Catégorie introuvable.")
                );

        Information info = InformationFactory.create(req, category);
        info.setOwner(currentUser);

        if (info.getAuthor() == null || info.getAuthor().isBlank()) {
            info.setAuthor(currentUser.getPseudo());
        } else {
            info.setAuthor(info.getAuthor().trim());
        }

        info.setTitle(info.getTitle().trim());
        info.setSlug(info.getSlug().trim());

        Information saved = repo.saveAndFlush(info);

        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public InformationResponse update(
            UUID id,
            InformationRequest req,
            User currentUser
    ) {
        validateInformationRequest(req);

        Information info = repo.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Information introuvable.")
                );

        checkCanManage(info, currentUser);

        Category category = categoryRepo.findById(req.getCategoryId())
                .orElseThrow(() ->
                        new NotFoundException("Catégorie introuvable.")
                );

        mapper.updateEntity(info, req, category);

        Information saved = repo.saveAndFlush(info);

        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id, User currentUser) {
        Information info = repo.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Information introuvable.")
                );

        checkCanManage(info, currentUser);

        repo.delete(info);
    }

    @Override
    @Transactional(readOnly = true)
    public InformationResponse findById(UUID id) {
        return repo.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() ->
                        new NotFoundException("Information introuvable.")
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InformationResponse> findAll(Pageable pageable) {
        return repo.findAll(pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InformationResponse> filter(
            InformationType type,
            UUID categoryId,
            Pageable pageable
    ) {
        return repo.filter(type, categoryId, pageable)
                .map(mapper::toResponse);
    }

@Override
@Transactional(readOnly = true)
public InformationResponse findBySlug(String slug) {
    if (slug == null || slug.isBlank()) {
        throw new BadRequestException(
                "Le slug est obligatoire."
        );
    }

    Information information = repo
            .findBySlug(slug.trim())
            .orElseThrow(() ->
                    new NotFoundException(
                            "Information introuvable."
                    )
            );

    return mapper.toResponse(information);
}



    @Override
    @Transactional(readOnly = true)
    public Page<InformationResponse> search(
            String keyword,
            Pageable pageable
    ) {
        return repo.search(keyword, pageable)
                .map(mapper::toResponse);
    }

    private void checkCanManage(
            Information info,
            User currentUser
    ) {
        if (currentUser == null) {
            throw new AccessDeniedException(
                    "Utilisateur non authentifié."
            );
        }

        boolean isAdmin = currentUser.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );

        if (isAdmin) {
            return;
        }

        if (
                info.getOwner() == null
                || info.getOwner().getUserId() == null
        ) {
            throw new AccessDeniedException(
                    "Vous ne pouvez pas modifier cette information."
            );
        }

        if (
                !info.getOwner()
                        .getUserId()
                        .equals(currentUser.getUserId())
        ) {
            throw new AccessDeniedException(
                    "Vous ne pouvez modifier que vos propres informations."
            );
        }
    }
}
