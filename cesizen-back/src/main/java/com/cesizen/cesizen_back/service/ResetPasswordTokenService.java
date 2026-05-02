package com.cesizen.cesizen_back.service;

import com.cesizen.cesizen_back.entity.ResetPasswordToken;
import com.cesizen.cesizen_back.entity.User;

public interface ResetPasswordTokenService {

    ResetPasswordToken create(User user);

    void createAndSendByEmail(User user);

    ResetPasswordToken validate(String value);

    void resetPassword(String tokenValue, String newPassword);
}