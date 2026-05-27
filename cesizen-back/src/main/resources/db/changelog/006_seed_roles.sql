-- liquibase formatted sql
-- changeset damien:006_seed_roles

INSERT INTO role (roleName)
VALUES ('USER'), ('ADMIN');