package com.cesizen.cesizen_back.repository;

import com.cesizen.cesizen_back.entity.Information;
import com.cesizen.cesizen_back.entity.InformationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InformationRepository extends JpaRepository<Information, String> {

    Page<Information> findByCategory_CategoryId(String categoryId, Pageable pageable);

    Page<Information> findByTypeAndCategory_CategoryId(
            InformationType type,
            String categoryId,
            Pageable pageable
    );

    Page<Information> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    Optional<Information> findBySlug(String slug);
}
