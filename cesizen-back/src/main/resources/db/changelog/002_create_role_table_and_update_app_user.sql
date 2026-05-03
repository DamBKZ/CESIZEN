-- liquibase formatted sql

-- changeset damien:002_create_role_and_update_user

CREATE TABLE role (
    roleID INT NOT NULL AUTO_INCREMENT,
    roleName VARCHAR(255) NOT NULL,
    PRIMARY KEY (roleID),
    UNIQUE KEY uq_role_name (roleName)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO role (roleName)
VALUES ('USER'), ('ADMIN');

ALTER TABLE user
ADD COLUMN roleID INT;

UPDATE user
SET roleID = (SELECT roleID FROM role WHERE roleName = 'USER')
WHERE roleID IS NULL;

ALTER TABLE user
MODIFY roleID INT NOT NULL;

ALTER TABLE user
ADD CONSTRAINT fk_user_role
FOREIGN KEY (roleID)
REFERENCES role(roleID)
ON DELETE RESTRICT
ON UPDATE CASCADE;