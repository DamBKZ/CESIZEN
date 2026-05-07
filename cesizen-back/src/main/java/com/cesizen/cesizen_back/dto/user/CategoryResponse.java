package com.cesizen.cesizen_back.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponse {

    private String categoryId;
    private String name;
    private String description;
    private String createdAt;
}
