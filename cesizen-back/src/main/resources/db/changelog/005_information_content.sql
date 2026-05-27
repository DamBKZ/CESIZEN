-- liquibase formatted sql
-- changeset damien:005_information_content

CREATE TABLE informationVideo (
    infoID CHAR(36) NOT NULL,
    infoVideoTitle VARCHAR(255) NOT NULL,
    infoVideoURL VARCHAR(255) NOT NULL,
    PRIMARY KEY (infoID),
    FOREIGN KEY (infoID) REFERENCES information(infoID) ON DELETE CASCADE
);

CREATE TABLE informationPdf (
    infoID CHAR(36) NOT NULL,
    infoPdfTitle VARCHAR(255) NOT NULL,
    infoPdfURL VARCHAR(255) NOT NULL,
    PRIMARY KEY (infoID),
    FOREIGN KEY (infoID) REFERENCES information(infoID) ON DELETE CASCADE
);

CREATE TABLE informationArticle (
    infoID CHAR(36) NOT NULL,
    infoArticleTitle VARCHAR(255) NOT NULL,
    infoArticleContent TEXT NOT NULL,
    PRIMARY KEY (infoID),
    FOREIGN KEY (infoID) REFERENCES information(infoID) ON DELETE CASCADE
);

CREATE TABLE information_tags (
    informationID CHAR(36) NOT NULL,
    tag VARCHAR(50) NOT NULL,
    FOREIGN KEY (informationID) REFERENCES information(infoID) ON DELETE CASCADE
);