package com.cesizen.cesizen_back.entity;

import java.util.Arrays;
import java.util.Optional;

public enum RoleName {

    USER,
    ADMIN;

    // -------------------------------------------------------------------------
    // HELPER
    // -------------------------------------------------------------------------

    public static Optional<RoleName> fromString(String value) {
        return Arrays.stream(values())
                .filter(r -> r.name().equalsIgnoreCase(value))
                .findFirst();
    }
}