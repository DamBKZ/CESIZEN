package com.cesizen.cesizen_back.mapper;

import com.cesizen.cesizen_back.dto.user.CategoryRequest;
import com.cesizen.cesizen_back.dto.user.CategoryResponse;
import com.cesizen.cesizen_back.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Category toEntity(CategoryRequest request);

    CategoryResponse toResponse(Category category);

    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(@MappingTarget Category category, CategoryRequest request);
}
