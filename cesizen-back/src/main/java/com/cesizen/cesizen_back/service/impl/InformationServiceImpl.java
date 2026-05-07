package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.entity.*;
import com.cesizen.cesizen_back.entity.InformationType;
import com.cesizen.cesizen_back.repository.InformationRepository;
import com.cesizen.cesizen_back.service.InformationService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InformationServiceImpl implements InformationService {

    private final InformationRepository informationRepository;

    @Override
    public Page<Information> findAll(Pageable pageable) {
        return informationRepository.findAll(pageable);
    }

    @Override
    public Page<Information> findByCategory(String categoryId, Pageable pageable) {
        return informationRepository.findByCategory_CategoryId(categoryId, pageable);
    }

    @Override
    public Page<Information> filter(InformationType type, String categoryId, Pageable pageable) {
        return informationRepository.findByTypeAndCategory_CategoryId(type, categoryId, pageable);
    }

    @Override
    public Page<Information> search(String keyword, Pageable pageable) {
        return informationRepository.findByTitleContainingIgnoreCase(keyword, pageable);
    }

    @Override
    public Information findById(String id) {
        return informationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Information introuvable."));
    }

    @Override
    public Information create(Information info) {
        return informationRepository.save(info);
    }

    @Override
    public Information update(String id, Information updated) {

        Information existing = findById(id);

        existing.setTitle(updated.getTitle());
        existing.setCategory(updated.getCategory());
        existing.setAuthor(updated.getAuthor());
        existing.setTags(updated.getTags());
        existing.setSlug(updated.getSlug());

        switch (existing.getType()) {
            case ARTICLE -> ((InformationArticle) existing).setContent(
                    ((InformationArticle) updated).getContent()
            );
            case VIDEO -> ((InformationVideo) existing).setVideoUrl(
                    ((InformationVideo) updated).getVideoUrl()
            );
            case PDF -> ((InformationPdf) existing).setPdfUrl(
                    ((InformationPdf) updated).getPdfUrl()
            );
        }

        return informationRepository.save(existing);
    }

    @Override
    public void delete(String id) {
        informationRepository.deleteById(id);
    }
}
