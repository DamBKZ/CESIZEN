package com.cesizen.cesizen_back.repository;

import com.cesizen.cesizen_back.entity.DiagnosticSurvey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagnosticSurveyRepository extends JpaRepository<DiagnosticSurvey, String> {

    List<DiagnosticSurvey> findByUserIdOrderByCreatedAtDesc(String userId);
}
