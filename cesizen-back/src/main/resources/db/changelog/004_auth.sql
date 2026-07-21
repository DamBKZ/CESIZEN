-- liquibase formatted sql
-- changeset damien:004_auth

CREATE TABLE refreshToken (
    refreshTokenID CHAR(36) NOT NULL DEFAULT (UUID()),
    userID CHAR(36) NOT NULL,
    refreshTokenValue VARCHAR(255) NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    refreshTokenCreatedDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    refreshTokenEndDate TIMESTAMP NOT NULL,
    PRIMARY KEY (refreshTokenID),
    UNIQUE KEY uq_refreshToken_value (refreshTokenValue),
    FOREIGN KEY (userID) REFERENCES users(userID) ON DELETE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE resetPasswordToken (
    resetPasswordTokenID CHAR(36) NOT NULL DEFAULT (UUID()),
    userID CHAR(36) NOT NULL,
    resetPasswordTokenValue VARCHAR(255) NOT NULL,
    resetPasswordTokenCreatedDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resetPasswordTokenEndDate TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (resetPasswordTokenID),
    UNIQUE KEY uq_resetPasswordToken_value (resetPasswordTokenValue),
    FOREIGN KEY (userID) REFERENCES users(userID) ON DELETE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;