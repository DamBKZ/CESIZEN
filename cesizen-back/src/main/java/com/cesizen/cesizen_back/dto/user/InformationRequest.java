package com.cesizen.cesizen_back.dto.user;

import com.cesizen.cesizen_back.entity.InformationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InformationRequest {

    @NotBlank
    private String title;

    @NotNull
    private InformationType type;

    @NotBlank
    private String categoryId;

    private String content;
    private String videoUrl;
    private String pdfUrl;

    @NotBlank
    private String author;

    private List<String> tags;

    private String slug;
}
