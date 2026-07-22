package com.cesizen.cesizen_back.factory;

import com.cesizen.cesizen_back.dto.user.InformationRequest;
import com.cesizen.cesizen_back.entity.*;

import java.util.ArrayList;

public class InformationFactory {

    public static Information create(InformationRequest req, Category category) {
        return switch (req.getType()) {
            case ARTICLE -> new InformationArticle(
                    req.getTitle(),
                    req.getAuthor(),
                    req.getSlug(),
                    req.getTags() != null ? req.getTags() : new ArrayList<>(),
                    category,
                    req.getContent()
            );
            case VIDEO -> new InformationVideo(
                    req.getTitle(),
                    req.getAuthor(),
                    req.getSlug(),
                    req.getTags() != null ? req.getTags() : new ArrayList<>(),
                    category,
                    req.getVideoUrl()
            );
            case PDF -> new InformationPdf(
                    req.getTitle(),
                    req.getAuthor(),
                    req.getSlug(),
                    req.getTags() != null ? req.getTags() : new ArrayList<>(),
                    category,
                    req.getPdfUrl()
            );
        };
    }
}
