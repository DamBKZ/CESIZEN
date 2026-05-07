package com.cesizen.cesizen_back.factory;

import com.cesizen.cesizen_back.entity.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InformationFactoryTest {

    @Test
    void createArticle_shouldReturnInformationArticle() {
        Category category = Category.builder().categoryId("123").name("Stress").build();

        Information info = InformationFactory.create(
                InformationType.ARTICLE,
                "Titre",
                "Contenu",
                null,
                null,
                category
        );

        assertTrue(info instanceof InformationArticle);
        assertEquals("Titre", info.getTitle());
        assertEquals("Contenu", ((InformationArticle) info).getContent());
        assertEquals(category, info.getCategory());
    }

    @Test
    void createVideo_shouldReturnInformationVideo() {
        Category category = Category.builder().categoryId("123").name("Stress").build();

        Information info = InformationFactory.create(
                InformationType.VIDEO,
                "Titre",
                null,
                "https://video.com",
                null,
                category
        );

        assertTrue(info instanceof InformationVideo);
        assertEquals("https://video.com", ((InformationVideo) info).getVideoUrl());
    }

    @Test
    void createPdf_shouldReturnInformationPdf() {
        Category category = Category.builder().categoryId("123").name("Stress").build();

        Information info = InformationFactory.create(
                InformationType.PDF,
                "Titre",
                null,
                null,
                "https://pdf.com",
                category
        );

        assertTrue(info instanceof InformationPdf);
        assertEquals("https://pdf.com", ((InformationPdf) info).getPdfUrl());
    }
}
