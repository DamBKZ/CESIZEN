package com.cesizen.cesizen_back.mapper;

import com.cesizen.cesizen_back.dto.user.CategoryRequest;
import com.cesizen.cesizen_back.dto.user.CategoryResponse;
import com.cesizen.cesizen_back.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // Convertit un DTO → entité
    Category toEntity(CategoryRequest request);

    // Convertit une entité → DTO
    CategoryResponse toResponse(Category category);

    // Met à jour une entité existante
    void updateEntity(@MappingTarget Category category, CategoryRequest request);
}
