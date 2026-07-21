-- liquibase formatted sql
-- changeset damien:003_diagnostic

CREATE TABLE diagnosticEvent (
    eventID CHAR(36) NOT NULL DEFAULT (UUID()),
    eventLabel VARCHAR(300) NOT NULL,
    eventLCU INT NOT NULL DEFAULT 0,
    PRIMARY KEY (eventID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE diagnosticSurvey (
    diagnosticSurveyID CHAR(36) NOT NULL DEFAULT (UUID()),
    userID CHAR(36) NOT NULL,
    score INT NOT NULL DEFAULT 0,
    riskLevel VARCHAR(50) NOT NULL DEFAULT 'LOW',
    diagnosticCreatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (diagnosticSurveyID),
    FOREIGN KEY (userID) REFERENCES users(userID) ON DELETE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE diagnosticAnswer (
    diagnosticAnswerID INT NOT NULL AUTO_INCREMENT,
    diagnosticSurveyID CHAR(36) NOT NULL,
    diagnosticEventID CHAR(36) NOT NULL,
    isChecked TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (diagnosticAnswerID),
    FOREIGN KEY (diagnosticSurveyID) REFERENCES diagnosticSurvey(diagnosticSurveyID) ON DELETE CASCADE,
    FOREIGN KEY (diagnosticEventID) REFERENCES diagnosticEvent(eventID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;