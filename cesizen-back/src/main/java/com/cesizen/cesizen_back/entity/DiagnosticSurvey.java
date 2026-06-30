package com.cesizen.cesizen_back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "diagnosticSurvey")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosticSurvey {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "surveyID", columnDefinition = "CHAR(36)", nullable = false, updatable = false)
    private String surveyId;

    @Column(name = "userID", columnDefinition = "CHAR(36)", nullable = false)
    private String userId;

    @CreationTimestamp
    @Column(name = "surveyCreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "surveyScore", nullable = false)
    private int score;

    @Column(name = "surveyRiskLevel", nullable = false, length = 50)
    private String riskLevel;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiagnosticAnswer> answers;
}
