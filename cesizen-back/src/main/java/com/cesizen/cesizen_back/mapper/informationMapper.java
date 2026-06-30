package com.cesizen.cesizen_back.mapper;

import com.cesizen.cesizen_back.dto.user.InformationRequest;
import com.cesizen.cesizen_back.dto.user.InformationResponse;
import com.cesizen.cesizen_back.entity.*;
import org.springframework.stereotype.Component;

@Component
public class InformationMapper {

    public InformationResponse toResponse(Information info) {
        return InformationResponse.builder()
                .informationId(info.getInformationId())
                .title(info.getTitle())
                .type(info.getType().name())
                .author(info.getAuthor())
                .slug(info.getSlug())
                .tags(info.getTags())
                .ownerId(info.getOwner() != null ? info.getOwner().getUserId() : null)
                .ownerPseudo(info.getOwner() != null ? info.getOwner().getPseudo() : null)
                .categoryId(info.getCategory().getCategoryId())
                .categoryName(info.getCategory().getName())
                .createdAt(info.getCreatedAt().toString())
                .content(info instanceof InformationArticle a ? a.getContent() : null)
                .videoUrl(info instanceof InformationVideo v ? v.getVideoUrl() : null)
                .pdfUrl(info instanceof InformationPdf p ? p.getPdfUrl() : null)
                .build();
    }

    public void updateEntity(Information info, InformationRequest req, Category category) {
        info.setTitle(req.getTitle());

        if (req.getAuthor() != null && !req.getAuthor().isBlank()) {
            info.setAuthor(req.getAuthor());
        }

        info.setSlug(req.getSlug());
        info.setTags(req.getTags() != null ? req.getTags() : java.util.List.of());
        info.setCategory(category);

        if (info instanceof InformationArticle a) {
            a.setContent(req.getContent());
        }

        if (info instanceof InformationVideo v) {
            v.setVideoUrl(req.getVideoUrl());
        }

        if (info instanceof InformationPdf p) {
            p.setPdfUrl(req.getPdfUrl());
        }
    }
}
