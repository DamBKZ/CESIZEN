-- liquibase formatted sql
-- changeset damien:001_init_schema

CREATE TABLE users (
    userID CHAR(36) NOT NULL DEFAULT (UUID()),
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    pseudo VARCHAR(30) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    userCreatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (userID),
    UNIQUE KEY uq_user_email (email),
    UNIQUE KEY uq_user_pseudo (pseudo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE role (
    roleID INT NOT NULL AUTO_INCREMENT,
    roleName VARCHAR(255) NOT NULL,
    PRIMARY KEY (roleID),
    UNIQUE KEY uq_role_name (roleName)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE category (
    categoryID CHAR(36) NOT NULL DEFAULT (UUID()),
    categoryName VARCHAR(255) NOT NULL,
    PRIMARY KEY (categoryID),
    UNIQUE KEY uq_category_name (categoryName)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE informationType (
    infoTypeID INT NOT NULL AUTO_INCREMENT,
    infoTypeLabel VARCHAR(255) NOT NULL,
    infoTypeCreatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (infoTypeID),
    UNIQUE KEY uq_infoType_label (infoTypeLabel)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE information (
    infoID CHAR(36) NOT NULL DEFAULT (UUID()),
    infoTitle VARCHAR(255) NOT NULL,
    categoryID CHAR(36) NOT NULL,
    infoTypeID INT NOT NULL,
    infoCreatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    infoUpdatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (infoID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;