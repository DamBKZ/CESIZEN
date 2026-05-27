package com.cesizen.cesizen_back.service;

import java.util.List;

import com.cesizen.cesizen_back.dto.user.AdviceRequest;
import com.cesizen.cesizen_back.dto.user.AdviceResponse;

public interface AdviceService {

    List<AdviceResponse> findAll();

    AdviceResponse findById(String id);

    AdviceResponse create(AdviceRequest request);

    AdviceResponse update(String id, AdviceRequest request);

    void delete(String id);

    List<String> getAdviceByLevel(String level);
}
