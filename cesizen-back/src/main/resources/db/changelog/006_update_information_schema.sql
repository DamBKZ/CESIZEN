-- liquibase formatted sql
-- changeset damien:006_update_information_schema

/* ============================================================
   CATEGORY 
   ============================================================ */

ALTER TABLE category
    MODIFY COLUMN categoryID CHAR(36) NOT NULL;

UPDATE category
SET categoryID = UUID()
WHERE LENGTH(categoryID) < 36;


/* ============================================================
   INFORMATION
   ============================================================ */

ALTER TABLE information
    MODIFY COLUMN infoID CHAR(36) NOT NULL;

UPDATE information
SET infoID = UUID()
WHERE LENGTH(infoID) < 36;

ALTER TABLE information
    MODIFY COLUMN infoTitle VARCHAR(255) NOT NULL,
    MODIFY COLUMN infoCreatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MODIFY COLUMN categoryID CHAR(36) NOT NULL;


/* ============================================================
   INFORMATION VIDEO 
   ============================================================ */

ALTER TABLE informationVideo
    MODIFY COLUMN infoID CHAR(36) NOT NULL;

UPDATE informationVideo v
JOIN information i ON v.infoID = i.infoID
SET v.infoID = i.infoID;


/* ============================================================
   INFORMATION PDF 
   ============================================================ */

ALTER TABLE informationPdf
    MODIFY COLUMN infoID CHAR(36) NOT NULL;

UPDATE informationPdf p
JOIN information i ON p.infoID = i.infoID
SET p.infoID = i.infoID;


/* ============================================================
   INFORMATION ARTICLE 
   ============================================================ */

ALTER TABLE informationArticle
    MODIFY COLUMN infoID CHAR(36) NOT NULL;

UPDATE informationArticle a
JOIN information i ON a.infoID = i.infoID
SET a.infoID = i.infoID;


/* ============================================================
   AJOUT TABLE TAGS
   ============================================================ */

CREATE TABLE information_tags (
    informationID CHAR(36) NOT NULL,
    tag VARCHAR(50) NOT NULL,
    CONSTRAINT fk_information_tags_information
        FOREIGN KEY (informationID)
        REFERENCES information(infoID)
        ON DELETE CASCADE
);
