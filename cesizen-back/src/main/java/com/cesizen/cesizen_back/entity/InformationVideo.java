package com.cesizen.cesizen_back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@DiscriminatorValue("VIDEO")
@Getter
@Setter
@NoArgsConstructor
public class InformationVideo extends Information {

    @Column(name = "informationVideoURL", length = 255)
    private String videoUrl;

    public InformationVideo(String title,
                            String author,
                            String slug,
                            List<String> tags,
                            Category category,
                            String videoUrl) {
        super(title, author, slug, tags, category);
        this.videoUrl = videoUrl;
        this.setType(InformationType.VIDEO);
    }
}
