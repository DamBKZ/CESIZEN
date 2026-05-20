package com.cesizen.cesizen_back.repository;

import com.cesizen.cesizen_back.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
