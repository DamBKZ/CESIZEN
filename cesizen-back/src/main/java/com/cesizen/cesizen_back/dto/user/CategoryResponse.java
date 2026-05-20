package com.cesizen.cesizen_back.dto.user;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategoryResponse {
    private UUID categoryId;
    private String name;
    private String description;
    private String createdAt;
}

