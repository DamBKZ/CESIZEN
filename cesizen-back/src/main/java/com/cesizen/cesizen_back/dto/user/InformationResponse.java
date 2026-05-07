package com.cesizen.cesizen_back.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class InformationResponse {

    private String informationId;
    private String title;
    private String type;

    private String categoryId;
    private String categoryName;

    private String createdAt;

    private String content;
    private String videoUrl;
    private String pdfUrl;

    private String author;
    private List<String> tags;
    private String slug;
}
