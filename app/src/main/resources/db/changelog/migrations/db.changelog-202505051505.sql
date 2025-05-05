--liquibase formatted sql
--changeset murillo:202504140012
--comment: set unlock_reason nullable

ALTER TABLE locks MODIFY COLUMN unlock_reason VARCHAR(255) NULL;

--rollback ALTER TABLE locks MODIFY COLUMN unlock_reason VARCHAR(255) NOT NULL;
