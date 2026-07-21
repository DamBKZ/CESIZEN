package com.cesizen.cesizen_back.dto.user;

import com.cesizen.cesizen_back.entity.InformationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class InformationRequest {

    @NotNull
    private InformationType type;

    @NotBlank
    private String title;

    private String author;
    private String slug;
    private List<String> tags;

    @NotNull
    private UUID categoryId;

    private String content;
    private String videoUrl;
    private String pdfUrl;
}
