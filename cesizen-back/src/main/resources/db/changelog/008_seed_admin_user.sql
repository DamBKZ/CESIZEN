-- liquibase formatted sql
-- changeset damien:008_seed_admin_user

INSERT INTO users (userID, email, password, pseudo, active, roleID)
VALUES (
    UUID(),
    'admin@amin.fr',
    '$2a$10$k1pXlTZthmCftxi0uBlAv.OY0o8Du72TpzlY/bOENkmMWD0xoyari',
    'admin',
    TRUE,
    (SELECT roleID FROM role WHERE roleName = 'ADMIN')
);