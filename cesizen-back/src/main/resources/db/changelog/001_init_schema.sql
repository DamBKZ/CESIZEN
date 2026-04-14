-- liquibase formatted sql 
-- changeset damien:001_init_schema

CREATE TABLE user (
    userID CHAR(36) NOT NULL DEFAULT (UUID()),
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    pseudo VARCHAR(255) NOT NULL,
    userCreatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (userID),
    UNIQUE KEY uq_user_email (email),
    UNIQUE KEY uq_user_pseudo (pseudo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE category (
    categoryID INT NOT NULL AUTO_INCREMENT,
    categoryName VARCHAR(255) NOT NULL,
    PRIMARY KEY (categoryID),
    UNIQUE KEY uq_category_name (categoryName)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE informationType (
    infoTypeID INT NOT NULL AUTO_INCREMENT,
    infoTypeLabel VARCHAR(255) NOT NULL,
    infoTypeCreatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (infoTypeID),
    UNIQUE KEY uq_infoType_label (infoTypeLabel)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE information (
    infoID INT NOT NULL AUTO_INCREMENT,
    infoTitle VARCHAR(255) NOT NULL,
    categoryID INT NOT NULL,
    infoTypeID INT NOT NULL,
    infoCreatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    infoUpdatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (infoID),
    CONSTRAINT fk_information_category
        FOREIGN KEY (categoryID)
            REFERENCES category(categoryID)
            ON DELETE RESTRICT
            ON UPDATE CASCADE,
    CONSTRAINT fk_information_type
        FOREIGN KEY (infoTypeID)
            REFERENCES informationType(infoTypeID)
            ON DELETE RESTRICT
            ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE informationVideo (
    infoID INT NOT NULL,
    infoVideoTitle VARCHAR(255) NOT NULL,
    infoVideoURL VARCHAR(255) NOT NULL,
    PRIMARY KEY (infoID),
    CONSTRAINT fk_infoVideo_information
        FOREIGN KEY (infoID)
            REFERENCES information(infoID)
            ON DELETE CASCADE
            ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE informationPdf (
    infoID INT NOT NULL,
    infoPdfTitle VARCHAR(255) NOT NULL,
    infoPdfURL VARCHAR(255) NOT NULL,
    PRIMARY KEY (infoID),
    CONSTRAINT fk_infoPdf_information
        FOREIGN KEY (infoID)
            REFERENCES information(infoID)
            ON DELETE CASCADE
            ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE informationArticle (
    infoID INT NOT NULL,
    infoArticleTitle VARCHAR(255) NOT NULL,
    infoArticleContent TEXT NOT NULL,
    PRIMARY KEY (infoID),
    CONSTRAINT fk_infoArticle_information
        FOREIGN KEY (infoID)
            REFERENCES information(infoID)
            ON DELETE CASCADE
            ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE diagnosticEvent (
    diagnosticEventID INT NOT NULL AUTO_INCREMENT,
    diagnosticEventName VARCHAR(255) NOT NULL,
    PRIMARY KEY (diagnosticEventID),
    UNIQUE KEY uq_diagnosticEvent_name (diagnosticEventName)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE diagnosticSurvey (
    diagnosticSurveyID CHAR(36) NOT NULL DEFAULT (UUID()),
    userID CHAR(36) NOT NULL,
    totalScore INT NOT NULL,
    diagnosticCreatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (diagnosticSurveyID),
    CONSTRAINT fk_diagnosticSurvey_user
        FOREIGN KEY (userID)
            REFERENCES user(userID)
            ON DELETE CASCADE
            ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE diagnosticAnswer (
    diagnosticAnswerID INT NOT NULL AUTO_INCREMENT,
    diagnosticSurveyID CHAR(36) NOT NULL,
    diagnosticEventID INT NOT NULL,
    diagnosticAnswerScore INT NOT NULL,
    PRIMARY KEY (diagnosticAnswerID),
    CONSTRAINT fk_diagnosticAnswer_survey
        FOREIGN KEY (diagnosticSurveyID)
            REFERENCES diagnosticSurvey(diagnosticSurveyID)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    CONSTRAINT fk_diagnosticAnswer_event
        FOREIGN KEY (diagnosticEventID)
            REFERENCES diagnosticEvent(diagnosticEventID)
            ON DELETE RESTRICT
            ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE logs (
    logID INT NOT NULL AUTO_INCREMENT,
    userID CHAR(36),
    logContent TEXT NOT NULL,
    logCreatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (logID),
    CONSTRAINT fk_logs_user
        FOREIGN KEY (userID)
            REFERENCES user(userID)
            ON DELETE SET NULL
            ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE refreshToken (
    refreshTokenID CHAR(36) NOT NULL DEFAULT (UUID()),
    userID CHAR(36) NOT NULL,
    refreshTokenValue VARCHAR(255) NOT NULL,
    refreshTokenCreatedDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    refreshTokenEndDate TIMESTAMP NOT NULL,
    PRIMARY KEY (refreshTokenID),
    UNIQUE KEY uq_refreshToken_value (refreshTokenValue),
    CONSTRAINT fk_refreshToken_user
        FOREIGN KEY (userID)
            REFERENCES user(userID)
            ON DELETE CASCADE
            ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE resetPasswordToken (
    resetTokenID CHAR(36) NOT NULL DEFAULT (UUID()),
    userID CHAR(36) NOT NULL,
    tokenValue VARCHAR(255) NOT NULL,
    tokenCreatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tokenEndDate TIMESTAMP NOT NULL,
    PRIMARY KEY (resetTokenID),
    UNIQUE KEY uq_resetToken_value (tokenValue),
    CONSTRAINT fk_resetPassword_user
        FOREIGN KEY (userID)
            REFERENCES user(userID)
            ON DELETE CASCADE
            ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
