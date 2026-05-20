package com.cesizen.cesizen_back.service;

import com.cesizen.cesizen_back.dto.user.CategoryRequest;
import com.cesizen.cesizen_back.dto.user.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    CategoryResponse create(CategoryRequest request);

    CategoryResponse update(UUID id, CategoryRequest request);

    void delete(UUID id);

    CategoryResponse findById(UUID id);

    List<CategoryResponse> findAll();
}

