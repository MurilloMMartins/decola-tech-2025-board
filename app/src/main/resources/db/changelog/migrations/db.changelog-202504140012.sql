--liquibase formatted sql
--changeset murillo:202504140012
--comment: boards table create

CREATE TABLE boards(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

--rollback DROP TABLE boards