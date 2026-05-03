package com.cesizen.cesizen_back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "refreshToken",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_refreshToken_value", columnNames = "refreshTokenValue")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @UuidGenerator
    @Column(name = "refreshTokenID", columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String refreshTokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "userID",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_refreshToken_user")
    )
    private User user;

    @Column(nullable = false, length = 255)
    private String refreshTokenValue;

    @Column(nullable = false, updatable = false)
    private LocalDateTime refreshTokenCreatedDate;

    @Column(nullable = false)
    private LocalDateTime refreshTokenEndDate;

    @Column(nullable = false)
    @Builder.Default
    private boolean revoked = false;

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    /**
     * Indique si le token est encore utilisable (non révoqué et non expiré).
     */
    public boolean isValid() {
        return !revoked && refreshTokenEndDate.isAfter(LocalDateTime.now());
    }
}