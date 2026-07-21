package com.cesizen.cesizen_back.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;

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
    @Column(
            name = "surveyID",
            columnDefinition = "CHAR(36)",
            nullable = false,
            updatable = false
    )
    private String surveyId;

    @Column(
            name = "userID",
            columnDefinition = "CHAR(36)",
            nullable = false
    )
    private String userId;

    @CreationTimestamp
    @Column(
            name = "surveyCreatedAt",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "surveyScore", nullable = false)
    private int score;

    @Column(
            name = "surveyRiskLevel",
            nullable = false,
            length = 50
    )
    private String riskLevel;

    @OneToMany(
            mappedBy = "survey",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
        @Builder.Default
        private List<DiagnosticAnswer> answers = new ArrayList<>();

    @PrePersist
    private void initializeCreatedAt() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
