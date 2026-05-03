-- liquibase formatted sql

-- changeset damien:003_update_schema

ALTER TABLE user
    ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE,
    MODIFY COLUMN pseudo VARCHAR(30) NOT NULL;

-- rollback ALTER TABLE user DROP COLUMN active, MODIFY COLUMN pseudo VARCHAR(255) NOT NULL;


ALTER TABLE refreshToken
    ADD COLUMN revoked BOOLEAN NOT NULL DEFAULT FALSE;

-- rollback ALTER TABLE refreshToken DROP COLUMN revoked;


ALTER TABLE resetPasswordToken
    CHANGE COLUMN resetTokenID resetPasswordTokenID CHAR(36) NOT NULL DEFAULT (UUID()),
    CHANGE COLUMN tokenValue resetPasswordTokenValue VARCHAR(255) NOT NULL,
    CHANGE COLUMN tokenCreatedAt resetPasswordTokenCreatedDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHANGE COLUMN tokenEndDate resetPasswordTokenEndDate TIMESTAMP NOT NULL,
    ADD COLUMN used BOOLEAN NOT NULL DEFAULT FALSE;

-- rollback ALTER TABLE resetPasswordToken CHANGE COLUMN resetPasswordTokenID resetTokenID CHAR(36) NOT NULL DEFAULT (UUID()), CHANGE COLUMN resetPasswordTokenValue tokenValue VARCHAR(255) NOT NULL, CHANGE COLUMN resetPasswordTokenCreatedDate tokenCreatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, CHANGE COLUMN resetPasswordTokenEndDate tokenEndDate TIMESTAMP NOT NULL, DROP COLUMN used;