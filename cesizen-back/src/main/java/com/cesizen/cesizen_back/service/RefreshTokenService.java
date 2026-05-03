package com.cesizen.cesizen_back.service;

import com.cesizen.cesizen_back.entity.RefreshToken;
import com.cesizen.cesizen_back.entity.User;

public interface RefreshTokenService {

    RefreshToken create(User user);

    RefreshToken validate(String value);

    void revoke(String value);

    void deleteByUserId(String userId);
}