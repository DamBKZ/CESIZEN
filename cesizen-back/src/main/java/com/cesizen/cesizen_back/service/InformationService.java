package com.cesizen.cesizen_back.service;

import com.cesizen.cesizen_back.entity.Information;
import com.cesizen.cesizen_back.entity.InformationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InformationService {

    Page<Information> findAll(Pageable pageable);

    Page<Information> findByCategory(String categoryId, Pageable pageable);

    Page<Information> filter(InformationType type, String categoryId, Pageable pageable);

    Page<Information> search(String keyword, Pageable pageable);

    Information findById(String id);

    Information create(Information info);

    Information update(String id, Information updated);

    void delete(String id);
}
