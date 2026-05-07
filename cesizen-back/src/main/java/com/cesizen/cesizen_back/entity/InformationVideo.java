package com.cesizen.cesizen_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("VIDEO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InformationVideo extends Information {

    @Column(name = "videoURL", length = 500)
    private String videoUrl;

    @Override
    public InformationType getType() {
        return InformationType.VIDEO;
    }
}
