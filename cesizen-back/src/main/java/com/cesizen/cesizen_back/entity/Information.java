package com.cesizen.cesizen_back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "informationKind",
        discriminatorType = DiscriminatorType.STRING
)
public abstract class Information {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(
            name = "informationID",
            columnDefinition = "CHAR(36)",
            updatable = false,
            nullable = false
    )
    private UUID informationId;

    @Column(
            name = "informationTitle",
            nullable = false,
            length = 150
    )
    private String title;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "categoryID",
            nullable = false
    )
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "userID",
            foreignKey = @ForeignKey(name = "fk_information_user")
    )
    private User owner;

    @CreationTimestamp
    @Column(
            name = "informationCreatedAt",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "informationAuthor",
            nullable = false,
            length = 100
    )
    private String author;

    @ElementCollection
    @CollectionTable(
            name = "information_tags",
            joinColumns = @JoinColumn(name = "informationID")
    )
    @Column(
            name = "tag",
            length = 50
    )
    private List<String> tags = new ArrayList<>();

    @Column(
            name = "informationSlug",
            nullable = false,
            unique = true,
            length = 200
    )
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "informationType",
            nullable = false,
            length = 20
    )
    private InformationType type;

    public Information(
            String title,
            String author,
            String slug,
            List<String> tags,
            Category category
    ) {
        this.title = title;
        this.author = author;
        this.slug = slug;
        this.tags = tags != null
                ? new ArrayList<>(tags)
                : new ArrayList<>();
        this.category = category;
    }

    @PrePersist
    protected void initializeCreatedAt() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (tags == null) {
            tags = new ArrayList<>();
        }
    }
}
