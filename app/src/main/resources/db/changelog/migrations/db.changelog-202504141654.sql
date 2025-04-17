--liquibase formatted sql
--changeset murillo:202504140012
--comment: locks table create

CREATE TABLE locks(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    locked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    locked_reason VARCHAR(255) NOT NULL,
    unlocked_at TIMESTAMP NULL,
    unlocked_reason VARCHAR(255) NOT NULL,
    card_id BIGINT NOT NULL,

    CONSTRAINT cards__locks_fk FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE
) ENGINE=InnoDB;

--rollback DROP TABLE locks