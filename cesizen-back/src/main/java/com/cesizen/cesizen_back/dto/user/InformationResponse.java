package com.cesizen.cesizen_back.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class InformationResponse {
    private UUID informationId;
    private String title;
    private String type;
    private String author;
    private String ownerId;
    private String ownerPseudo;

    private String slug;
    private List<String> tags;

    private UUID categoryId;
    private String categoryName;

    private String createdAt;

    private String content;
    private String videoUrl;
    private String pdfUrl;
}