-- liquibase formatted sql
-- changeset damien:002_relations

-- user role
ALTER TABLE user
ADD COLUMN roleID INT NOT NULL;

ALTER TABLE user
ADD CONSTRAINT fk_user_role
FOREIGN KEY (roleID)
REFERENCES role(roleID)
ON DELETE RESTRICT
ON UPDATE CASCADE;

-- information relations
ALTER TABLE information
ADD CONSTRAINT fk_information_category
FOREIGN KEY (categoryID)
REFERENCES category(categoryID)
ON DELETE RESTRICT
ON UPDATE CASCADE;

ALTER TABLE information
ADD CONSTRAINT fk_information_type
FOREIGN KEY (infoTypeID)
REFERENCES informationType(infoTypeID)
ON DELETE RESTRICT
ON UPDATE CASCADE;