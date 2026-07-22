package com.cesizen.cesizen_back.factory;

import com.cesizen.cesizen_back.dto.user.InformationRequest;
import com.cesizen.cesizen_back.entity.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InformationFactoryTest {

    private Category buildCategory() {
        return Category.builder()
                .categoryId(UUID.randomUUID())
                .name("Sport")
                .build();
    }

    // ------------------------------------------------------------
    // ARTICLE
    // ------------------------------------------------------------
    @Test
    void create_shouldReturnInformationArticle() {
        Category category = buildCategory();

        InformationRequest req = new InformationRequest();
        req.setType(InformationType.ARTICLE);
        req.setTitle("Titre A");
        req.setAuthor("Auteur A");
        req.setSlug("slug-a");
        req.setTags(List.of("t1"));
        req.setCategoryId(category.getCategoryId());
        req.setContent("Contenu article");

        Information info = InformationFactory.create(req, category);

        assertTrue(info instanceof InformationArticle);
        InformationArticle a = (InformationArticle) info;

        assertEquals("Titre A", a.getTitle());
        assertEquals("Auteur A", a.getAuthor());
        assertEquals("slug-a", a.getSlug());
        assertEquals("Contenu article", a.getContent());
        assertEquals(category, a.getCategory());
    }

    // ------------------------------------------------------------
    // VIDEO
    // ------------------------------------------------------------
    @Test
    void create_shouldReturnInformationVideo() {
        Category category = buildCategory();

        InformationRequest req = new InformationRequest();
        req.setType(InformationType.VIDEO);
        req.setTitle("Titre V");
        req.setAuthor("Auteur V");
        req.setSlug("slug-v");
        req.setTags(List.of("t2"));
        req.setCategoryId(category.getCategoryId());
        req.setVideoUrl("https://video.test");

        Information info = InformationFactory.create(req, category);

        assertTrue(info instanceof InformationVideo);
        InformationVideo v = (InformationVideo) info;

        assertEquals("Titre V", v.getTitle());
        assertEquals("Auteur V", v.getAuthor());
        assertEquals("slug-v", v.getSlug());
        assertEquals("https://video.test", v.getVideoUrl());
        assertEquals(category, v.getCategory());
    }

    // ------------------------------------------------------------
    // PDF
    // ------------------------------------------------------------
    @Test
    void create_shouldReturnInformationPdf() {
        Category category = buildCategory();

        InformationRequest req = new InformationRequest();
        req.setType(InformationType.PDF);
        req.setTitle("Titre P");
        req.setAuthor("Auteur P");
        req.setSlug("slug-p");
        req.setTags(List.of("t3"));
        req.setCategoryId(category.getCategoryId());
        req.setPdfUrl("https://pdf.test");

        Information info = InformationFactory.create(req, category);

        assertTrue(info instanceof InformationPdf);
        InformationPdf p = (InformationPdf) info;

        assertEquals("Titre P", p.getTitle());
        assertEquals("Auteur P", p.getAuthor());
        assertEquals("slug-p", p.getSlug());
        assertEquals("https://pdf.test", p.getPdfUrl());
        assertEquals(category, p.getCategory());
    }
}
