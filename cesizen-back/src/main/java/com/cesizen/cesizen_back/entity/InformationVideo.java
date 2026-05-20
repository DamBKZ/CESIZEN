package com.cesizen.cesizen_back.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("VIDEO")
@Getter
@Setter
@NoArgsConstructor
public class InformationVideo extends Information {

    @Column(name = "informationVideoUrl", length = 500)
    private String videoUrl;

    @Override
    public InformationType getType() {
        return InformationType.VIDEO;
    }

    public InformationVideo(String title, String author, String slug, List<String> tags,
                            Category category, String videoUrl) {
        super(null, title, category, null, author, tags, slug);
        this.videoUrl = videoUrl;
    }
}

