-- liquibase formatted sql
-- changeset damien:001_init_schema

CREATE TABLE role (
    roleID INT NOT NULL AUTO_INCREMENT,
    roleName VARCHAR(50) NOT NULL,
    PRIMARY KEY (roleID),
    UNIQUE KEY uq_role_name (roleName)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE users (
    userID CHAR(36) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    pseudo VARCHAR(30) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    userCreatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    roleID INT NOT NULL,
    PRIMARY KEY (userID),
    UNIQUE KEY uq_user_email (email),
    UNIQUE KEY uq_user_pseudo (pseudo),
    CONSTRAINT fk_user_role
        FOREIGN KEY (roleID)
        REFERENCES role(roleID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE category (
    categoryID CHAR(36) NOT NULL,
    categoryName VARCHAR(100) NOT NULL,
    categoryDescription VARCHAR(500) NULL,
    categoryCreatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (categoryID),
    UNIQUE KEY uq_category_name (categoryName)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE information (
    informationID CHAR(36) NOT NULL,
    informationKind VARCHAR(31) NOT NULL,
    informationTitle VARCHAR(150) NOT NULL,
    categoryID CHAR(36) NOT NULL,
    informationCreatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    informationAuthor VARCHAR(100) NOT NULL,
    informationSlug VARCHAR(200) NOT NULL,
    informationType VARCHAR(20) NOT NULL,
    informationContent TEXT NULL,
    informationVideoURL VARCHAR(255) NULL,
    informationPdfURL VARCHAR(255) NULL,
    PRIMARY KEY (informationID),
    UNIQUE KEY uq_information_slug (informationSlug),
    CONSTRAINT fk_information_category
        FOREIGN KEY (categoryID)
        REFERENCES category(categoryID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE information_tags (
    informationID CHAR(36) NOT NULL,
    tag VARCHAR(50) NOT NULL,
    CONSTRAINT fk_information_tags_information
        FOREIGN KEY (informationID)
        REFERENCES information(informationID)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
