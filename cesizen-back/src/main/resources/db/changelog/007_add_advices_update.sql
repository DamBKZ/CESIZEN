-- liquibase formatted sql
-- changeset damien:007_add_advices_update

/* ============================================================
   ADVICES 
   ============================================================ */
CREATE TABLE advice (
    id CHAR(36) PRIMARY KEY,
    level VARCHAR(20) NOT NULL,
    message TEXT NOT NULL
);

INSERT INTO advice (id, level, message) VALUES
(UUID(), 'FAIBLE', 'Continuez vos bonnes habitudes.'),
(UUID(), 'FAIBLE', 'Prenez 5 minutes par jour pour respirer profondément.'),
(UUID(), 'FAIBLE', 'Gardez un bon équilibre entre travail et repos.'),
(UUID(), 'FAIBLE', 'Identifiez les sources de stress mineures avant qu’elles ne s’accumulent.'),
(UUID(), 'FAIBLE', 'Maintenez une bonne hygiène de sommeil.'),
(UUID(), 'FAIBLE', 'Hydratez-vous régulièrement.'),
(UUID(), 'FAIBLE', 'Faites une courte marche quotidienne.'),
(UUID(), 'FAIBLE', 'Refaites un diagnostic dans 15 jours.');

INSERT INTO advice (id, level, message) VALUES
(UUID(), 'MODERE', 'Faites une pause de 10 minutes toutes les 2 heures.'),
(UUID(), 'MODERE', 'Pratiquez une activité relaxante (marche, respiration, musique).'),
(UUID(), 'MODERE', 'Réduisez les engagements non essentiels.'),
(UUID(), 'MODERE', 'Parlez à un proche ou collègue de confiance.'),
(UUID(), 'MODERE', 'Organisez vos tâches par priorité.'),
(UUID(), 'MODERE', 'Évitez les écrans 30 minutes avant de dormir.'),
(UUID(), 'MODERE', 'Consultez les articles “Gestion du stress” dans l’app.'),
(UUID(), 'MODERE', 'Refaites un diagnostic dans 7 jours.');

INSERT INTO advice (id, level, message) VALUES
(UUID(), 'ELEVE', 'Prenez un moment pour souffler immédiatement.'),
(UUID(), 'ELEVE', 'Identifiez la source principale de votre stress.'),
(UUID(), 'ELEVE', 'Réduisez les tâches non urgentes pendant 48h.'),
(UUID(), 'ELEVE', 'Parlez à quelqu’un de confiance dès que possible.'),
(UUID(), 'ELEVE', 'Faites un exercice de respiration profonde pendant 3 minutes.'),
(UUID(), 'ELEVE', 'Évitez les décisions importantes dans les prochaines 24h.'),
(UUID(), 'ELEVE', 'Consultez un professionnel si le stress devient ingérable.'),
(UUID(), 'ELEVE', 'Refaites un diagnostic dans 48h.');
