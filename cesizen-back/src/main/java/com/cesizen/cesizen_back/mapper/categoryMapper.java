package com.cesizen.cesizen_back.mapper;

import com.cesizen.cesizen_back.dto.user.CategoryRequest;
import com.cesizen.cesizen_back.dto.user.CategoryResponse;
import com.cesizen.cesizen_back.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class categoryMapper {

    public CategoryResponse toResponse(Category c) {
        return CategoryResponse.builder()
                .categoryId(c.getCategoryId())
                .name(c.getName())
                .description(c.getDescription())
                .createdAt(c.getCreatedAt().toString())
                .build();
    }

    public void updateEntity(Category c, CategoryRequest req) {
        c.setName(req.getName());
        c.setDescription(req.getDescription());
    }
}
