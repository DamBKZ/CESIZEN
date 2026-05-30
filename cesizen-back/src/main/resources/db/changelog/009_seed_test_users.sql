-- liquibase formatted sql
-- changeset damien:009_seed_test_users

INSERT IGNORE INTO users (userID, email, password, pseudo, active, roleID)
VALUES
(
    UUID(),
    'admin@admin.fr',
    '$2a$10$k1pXlTZthmCftxi0uBlAv.OY0o8Du72TpzlY/bOENkmMWD0xoyari',
    'admin_alias',
    TRUE,
    (SELECT roleID FROM role WHERE roleName = 'ADMIN')
),
(
    UUID(),
    'damien@damien.fr',
    '$2a$10$k1pXlTZthmCftxi0uBlAv.OY0o8Du72TpzlY/bOENkmMWD0xoyari',
    'damien',
    TRUE,
    (SELECT roleID FROM role WHERE roleName = 'USER')
);