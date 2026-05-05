package com.cesizen.cesizen_back.dto.user;

public record ValidRefreshToken(
        String userId,
        String roleName
) {}
