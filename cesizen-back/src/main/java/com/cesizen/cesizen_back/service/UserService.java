package com.cesizen.cesizen_back.service;

import com.cesizen.cesizen_back.entity.User;

import java.util.List;

public interface UserService {

    User register(String email, String password, String pseudo);

    User login(String email, String rawPassword);

    User findByEmail(String email);

    User findById(String userId);

    User updateUserProfile(String userId, String email, String pseudo);

    void changePassword(String userId, String currentPassword, String newPassword);

    // -------------------------------------------------------------------------
    // ADMIN
    // -------------------------------------------------------------------------

    List<User> findAll();

    void deactivate(String userId);

    void activate(String userId);

    void delete(String userId);
}