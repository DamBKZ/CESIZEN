-- liquibase formatted sql
-- changeset damien:011_create_logs_table

CREATE TABLE logs (
    logId INT NOT NULL AUTO_INCREMENT,
    userID CHAR(36) NULL,
    logContent TEXT NOT NULL,
    logCreatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (logId),
    INDEX idx_logs_userID (userID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;