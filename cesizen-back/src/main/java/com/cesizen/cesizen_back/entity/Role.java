package com.cesizen.cesizen_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "role")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roleID", updatable = false, nullable = false)
    private Integer roleId;

    @Column(name = "roleName", nullable = false, unique = true, length = 50)
    private String roleName;
}