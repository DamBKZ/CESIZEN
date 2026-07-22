package com.cesizen.cesizen_back.service;

import com.cesizen.cesizen_back.dto.user.DiagnosticHistoryResponse;
import com.cesizen.cesizen_back.dto.user.DiagnosticResponse;
import com.cesizen.cesizen_back.dto.user.DiagnosticSubmitRequest;
import com.cesizen.cesizen_back.entity.User;

import java.util.List;

public interface DiagnosticService {

    DiagnosticResponse submit(DiagnosticSubmitRequest request, User currentUser);

    List<DiagnosticHistoryResponse> history(User currentUser);
}
