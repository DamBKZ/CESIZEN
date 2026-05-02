package com.cesizen.cesizen_back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "resetPasswordToken",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_resetPasswordToken_value", columnNames = "resetPasswordTokenValue")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordToken {

    @Id
    @UuidGenerator
    @Column(name = "resetPasswordTokenID", columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String resetPasswordTokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "userID",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_resetPasswordToken_user")
    )
    private User user;

    @Column(nullable = false, length = 255)
    private String resetPasswordTokenValue;

    @Column(nullable = false, updatable = false)
    private LocalDateTime resetPasswordTokenCreatedDate;

    @Column(nullable = false)
    private LocalDateTime resetPasswordTokenEndDate;

    @Column(nullable = false)
    @Builder.Default
    private boolean used = false;

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    /**
     * Indique si le token est encore utilisable (non consommé et non expiré).
     */
    public boolean isValid() {
        return !used && resetPasswordTokenEndDate.isAfter(LocalDateTime.now());
    }
}