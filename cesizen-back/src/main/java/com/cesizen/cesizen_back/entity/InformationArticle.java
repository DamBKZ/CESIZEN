package com.cesizen.cesizen_back.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("ARTICLE")
@Getter
@Setter
@NoArgsConstructor
public class InformationArticle extends Information {

    @Column(name = "informationContent", columnDefinition = "TEXT")
    private String content;

    @Override
    public InformationType getType() {
        return InformationType.ARTICLE;
    }

    public InformationArticle(String title, String author, String slug, List<String> tags,
                              Category category, String content) {
        super(null, title, category, null, author, tags, slug);
        this.content = content;
    }
}

