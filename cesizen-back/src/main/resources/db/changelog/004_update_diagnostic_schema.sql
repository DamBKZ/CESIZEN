-- liquibase formatted sql
-- changeset damien:004_update_diagnostic_schema

ALTER TABLE diagnosticEvent
    DROP PRIMARY KEY,
    CHANGE COLUMN diagnosticEventID eventID CHAR(36) NOT NULL DEFAULT (UUID()),
    CHANGE COLUMN diagnosticEventName eventLabel VARCHAR(300) NOT NULL,
    ADD COLUMN eventLCU INT NOT NULL DEFAULT 0,
    ADD PRIMARY KEY (eventID);

-- rollback ALTER TABLE diagnosticEvent
-- DROP PRIMARY KEY,
-- CHANGE COLUMN eventID diagnosticEventID INT NOT NULL AUTO_INCREMENT,
-- CHANGE COLUMN eventLabel diagnosticEventName VARCHAR(255) NOT NULL,
-- DROP COLUMN eventLCU,
-- ADD PRIMARY KEY (diagnosticEventID);

ALTER TABLE diagnosticSurvey
    DROP COLUMN totalScore,
    ADD COLUMN score INT NOT NULL DEFAULT 0,
    ADD COLUMN riskLevel VARCHAR(50) NOT NULL DEFAULT 'LOW';

-- rollback ALTER TABLE diagnosticSurvey
-- DROP COLUMN score,
-- DROP COLUMN riskLevel,
-- ADD COLUMN totalScore INT NOT NULL;

ALTER TABLE diagnosticAnswer
    DROP COLUMN diagnosticAnswerScore,
    ADD COLUMN isChecked TINYINT(1) NOT NULL DEFAULT 0;

-- rollback ALTER TABLE diagnosticAnswer
-- DROP COLUMN isChecked,
-- ADD COLUMN diagnosticAnswerScore INT NOT NULL;
