package com.cesizen.cesizen_back.service;

import com.cesizen.cesizen_back.entity.User;

public interface UserService {

    User register(String email, String password, String pseudo);

    User login(String email, String rawPassword);

    User findByEmail(String email);

    User updateUserProfile(String userId, String email, String pseudo);
}
