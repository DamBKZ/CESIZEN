package com.cesizen.cesizen_back.service;

import com.cesizen.cesizen_back.entity.Category;

import java.util.List;

public interface CategoryService {

    List<Category> findAll();

    Category findById(String id);

    Category create(Category category);

    Category update(String id, Category updated);

    void delete(String id);
}
