package com.cesizen.cesizen_back.service;

public interface EmailService {
    void sendResetPasswordEmail(String to, String token);
    void sendDiagnosticResultEmail(String to, int score, String riskLevel, String date);
    void sendAccountDeletionEmail(String email, String pseudo);
}
