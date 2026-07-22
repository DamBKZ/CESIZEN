package com.cesizen.cesizen_back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "diagnosticAnswer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosticAnswer {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "answerID", columnDefinition = "CHAR(36)", nullable = false, updatable = false)
    private String answerId;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "surveyID", nullable = false)
    private DiagnosticSurvey survey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eventID", nullable = false)
    private DiagnosticEvent event;

    @Column(name = "isChecked", nullable = false)
    private boolean checked;
}
