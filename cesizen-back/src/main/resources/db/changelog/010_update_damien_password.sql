-- liquibase formatted sql
-- changeset damien:010_update_damien_password

UPDATE users
SET password = '$2b$10$RNmvajFAqayiJ8L.T1HOmOb/jgQ6iOzfb.zi7VatMT3RFgZ8fwWXu'
WHERE email = 'damien@damien.fr';