-- liquibase formatted sql
-- changeset damien:012_information_owner

ALTER TABLE information
ADD COLUMN userID CHAR(36) NULL;

ALTER TABLE information
ADD CONSTRAINT fk_information_user
FOREIGN KEY (userID)
REFERENCES users(userID)
ON DELETE SET NULL
ON UPDATE CASCADE;
