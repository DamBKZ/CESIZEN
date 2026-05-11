package com.cesizen.cesizen_back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "diagnosticEvent")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosticEvent {

    @Id
    @UuidGenerator
    @Column(name = "eventID", columnDefinition = "CHAR(36)", nullable = false, updatable = false)
    private String eventId;

    @Column(name = "eventLabel", nullable = false, length = 300)
    private String label;

    @Column(name = "eventLCU", nullable = false)
    private int lcu; // Life Change Unit
}

