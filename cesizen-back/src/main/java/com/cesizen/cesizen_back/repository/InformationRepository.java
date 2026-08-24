package com.cesizen.cesizen_back.repository;

import com.cesizen.cesizen_back.entity.Information;
import com.cesizen.cesizen_back.entity.InformationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface InformationRepository extends JpaRepository<Information, UUID> {

       boolean existsByCategory_CategoryId(UUID categoryId);

       Optional<Information> findBySlug(String slug);


    @Query("""
           SELECT i FROM Information i
           WHERE LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(i.slug) LIKE LOWER(CONCAT('%', :keyword, '%'))
           """)
    Page<Information> search(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
           SELECT i FROM Information i
           WHERE (:type IS NULL OR i.type = :type)
             AND (:categoryId IS NULL OR i.category.categoryId = :categoryId)
           """)
    Page<Information> filter(@Param("type") InformationType type,
                             @Param("categoryId") UUID categoryId,
                             Pageable pageable);
}
