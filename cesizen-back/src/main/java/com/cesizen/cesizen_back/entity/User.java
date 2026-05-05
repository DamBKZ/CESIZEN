package com.cesizen.cesizen_back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(
    name = "user",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_user_email", columnNames = "email"),
        @UniqueConstraint(name = "uq_user_pseudo", columnNames = "pseudo")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @UuidGenerator
    @Column(name = "userID", columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String userId;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, unique = true, length = 30)
    private String pseudo;

    @Column(name = "userCreatedAt", nullable = false, updatable = false)
    private LocalDateTime userCreatedAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "roleID",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_user_role")
    )
    private Role role;

    // -------------------------------------------------------------------------
    // UserDetails (Spring Security)
    // -------------------------------------------------------------------------

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}