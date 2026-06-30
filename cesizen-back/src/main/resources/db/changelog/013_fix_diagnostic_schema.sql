-- liquibase formatted sql
-- changeset damien:013_fix_diagnostic_schema

-- Recréation des tables diagnosticAnswer et diagnosticSurvey
-- pour alignement avec les entités JPA actuelles.

DROP TABLE IF EXISTS diagnosticAnswer;

DROP TABLE IF EXISTS diagnosticSurvey;

CREATE TABLE diagnosticSurvey (
    surveyID CHAR(36) NOT NULL,
    userID CHAR(36) NOT NULL,
    surveyCreatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    surveyScore INT NOT NULL DEFAULT 0,
    surveyRiskLevel VARCHAR(50) NOT NULL DEFAULT 'LOW',
    PRIMARY KEY (surveyID),
    CONSTRAINT fk_diagnosticSurvey_user
        FOREIGN KEY (userID)
        REFERENCES users(userID)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE diagnosticAnswer (
    answerID CHAR(36) NOT NULL,
    surveyID CHAR(36) NOT NULL,
    eventID CHAR(36) NOT NULL,
    isChecked BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (answerID),
    CONSTRAINT fk_diagnosticAnswer_survey
        FOREIGN KEY (surveyID)
        REFERENCES diagnosticSurvey(surveyID)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_diagnosticAnswer_event
        FOREIGN KEY (eventID)
        REFERENCES diagnosticEvent(eventID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
