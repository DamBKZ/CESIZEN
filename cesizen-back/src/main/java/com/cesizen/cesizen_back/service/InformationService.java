package com.cesizen.cesizen_back.service;

import com.cesizen.cesizen_back.dto.user.InformationRequest;
import com.cesizen.cesizen_back.dto.user.InformationResponse;
import com.cesizen.cesizen_back.entity.InformationType;
import com.cesizen.cesizen_back.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface InformationService {

    InformationResponse create(InformationRequest request, User currentUser);

    InformationResponse update(UUID id, InformationRequest request, User currentUser);

    void delete(UUID id, User currentUser);

    InformationResponse findById(UUID id);

    Page<InformationResponse> findAll(Pageable pageable);

    Page<InformationResponse> filter(InformationType type, UUID categoryId, Pageable pageable);

    Page<InformationResponse> search(String keyword, Pageable pageable);
}
