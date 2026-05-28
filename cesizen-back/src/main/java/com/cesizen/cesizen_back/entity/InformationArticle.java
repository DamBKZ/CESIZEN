package com.cesizen.cesizen_back.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@DiscriminatorValue("ARTICLE")
@Getter
@Setter
@NoArgsConstructor
public class InformationArticle extends Information {

    @Column(name = "informationContent", columnDefinition = "TEXT")
    private String content;

    public InformationArticle(String title,
                              String author,
                              String slug,
                              List<String> tags,
                              Category category,
                              String content) {
        super(title, author, slug, tags, category);
        this.content = content;
        this.setType(InformationType.ARTICLE);
    }
}
