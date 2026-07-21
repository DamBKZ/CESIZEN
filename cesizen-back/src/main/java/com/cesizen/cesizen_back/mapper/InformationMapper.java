package com.cesizen.cesizen_back.mapper;

import com.cesizen.cesizen_back.dto.user.InformationRequest;
import com.cesizen.cesizen_back.dto.user.InformationResponse;
import com.cesizen.cesizen_back.entity.Category;
import com.cesizen.cesizen_back.entity.Information;
import com.cesizen.cesizen_back.entity.InformationArticle;
import com.cesizen.cesizen_back.entity.InformationPdf;
import com.cesizen.cesizen_back.entity.InformationVideo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class InformationMapper {

    public InformationResponse toResponse(Information info) {
        if (info == null) {
            return null;
        }

        return InformationResponse.builder()
                .informationId(info.getInformationId())
                .title(info.getTitle())
                .type(
                        info.getType() != null
                                ? info.getType().name()
                                : null
                )
                .author(info.getAuthor())
                .slug(info.getSlug())
                .tags(
                        info.getTags() != null
                                ? new ArrayList<>(info.getTags())
                                : new ArrayList<>()
                )
                .ownerId(
                        info.getOwner() != null
                                ? info.getOwner().getUserId()
                                : null
                )
                .ownerPseudo(
                        info.getOwner() != null
                                ? info.getOwner().getPseudo()
                                : null
                )
                .categoryId(
                        info.getCategory() != null
                                ? info.getCategory().getCategoryId()
                                : null
                )
                .categoryName(
                        info.getCategory() != null
                                ? info.getCategory().getName()
                                : null
                )
                .createdAt(
                        info.getCreatedAt() != null
                                ? info.getCreatedAt().toString()
                                : null
                )
                .content(
                        info instanceof InformationArticle article
                                ? article.getContent()
                                : null
                )
                .videoUrl(
                        info instanceof InformationVideo video
                                ? video.getVideoUrl()
                                : null
                )
                .pdfUrl(
                        info instanceof InformationPdf pdf
                                ? pdf.getPdfUrl()
                                : null
                )
                .build();
    }

    public void updateEntity(
            Information info,
            InformationRequest req,
            Category category
    ) {
        info.setTitle(req.getTitle().trim());

        if (req.getAuthor() != null && !req.getAuthor().isBlank()) {
            info.setAuthor(req.getAuthor().trim());
        }

        info.setSlug(req.getSlug().trim());

        info.setTags(
                req.getTags() != null
                        ? new ArrayList<>(req.getTags())
                        : new ArrayList<>()
        );

        info.setCategory(category);

        if (info instanceof InformationArticle article) {
            article.setContent(req.getContent());
        }

        if (info instanceof InformationVideo video) {
            video.setVideoUrl(req.getVideoUrl());
        }

        if (info instanceof InformationPdf pdf) {
            pdf.setPdfUrl(req.getPdfUrl());
        }
    }
}
