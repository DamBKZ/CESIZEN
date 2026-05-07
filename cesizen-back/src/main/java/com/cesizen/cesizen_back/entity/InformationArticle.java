package com.cesizen.cesizen_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("ARTICLE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InformationArticle extends Information {

    @Column(name = "articleContent", columnDefinition = "TEXT")
    private String content;

    @Override
    public InformationType getType() {
        return InformationType.ARTICLE;
    }
}
