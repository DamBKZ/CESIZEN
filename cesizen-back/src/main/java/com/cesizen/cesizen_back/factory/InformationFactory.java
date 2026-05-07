package com.cesizen.cesizen_back.factory;

import com.cesizen.cesizen_back.entity.*;

public class InformationFactory {

    public static Information create(
            InformationType type,
            String title,
            String content,
            String videoUrl,
            String pdfUrl,
            Category category
    ) {
        return switch (type) {

            case ARTICLE -> {
                InformationArticle article = new InformationArticle();
                article.setTitle(title);
                article.setCategory(category);
                article.setContent(content);
                yield article;
            }

            case VIDEO -> {
                InformationVideo video = new InformationVideo();
                video.setTitle(title);
                video.setCategory(category);
                video.setVideoUrl(videoUrl);
                yield video;
            }

            case PDF -> {
                InformationPdf pdf = new InformationPdf();
                pdf.setTitle(title);
                pdf.setCategory(category);
                pdf.setPdfUrl(pdfUrl);
                yield pdf;
            }
        };
    }
}
