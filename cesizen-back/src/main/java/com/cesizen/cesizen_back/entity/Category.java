package com.cesizen.cesizen_back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "category",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_category_name", columnNames = "categoryName")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @UuidGenerator
    @Column(name = "categoryID", columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private UUID categoryId;

    @Column(name = "categoryName", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "categoryDescription", length = 500)
    private String description;

    @CreationTimestamp
    @Column(name = "categoryCreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
