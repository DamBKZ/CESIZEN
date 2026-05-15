package com.cesizen.cesizen_back.service;

public interface EmailService {

    /**
     * Envoie un email de réinitialisation de mot de passe.
     *
     * @param to    Adresse email du destinataire
     * @param token Token de réinitialisation à inclure dans le lien
     */
    void sendResetPasswordEmail(String to, String token);

    /**
     * Envoie un email contenant les résultats d’un diagnostic.
     *
     * @param to        Adresse email du destinataire
     * @param score     Score obtenu
     * @param riskLevel Niveau de risque
     * @param date      Date du diagnostic
     */
    void sendDiagnosticResultEmail(String to, int score, String riskLevel, String date);
}
