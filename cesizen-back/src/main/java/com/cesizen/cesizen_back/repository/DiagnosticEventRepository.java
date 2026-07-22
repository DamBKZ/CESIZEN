package com.cesizen.cesizen_back.repository;

import com.cesizen.cesizen_back.entity.DiagnosticEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosticEventRepository extends JpaRepository<DiagnosticEvent, String> {
}
