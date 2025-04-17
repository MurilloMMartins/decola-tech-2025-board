--liquibase formatted sql
--changeset murillo:202504140012
--comment: boards_columns table create

CREATE TABLE boards_columns(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    `order` INT NOT NULL,
    type VARCHAR(10) NOT NULL,
    board_id BIGINT NOT NULL,

    CONSTRAINT boards__boards_columns_fk FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE CASCADE,
    CONSTRAINT id_order_uk UNIQUE KEY unique_board_id_order (board_id, `order`)
) ENGINE=InnoDB;

--rollback DROP TABLE boards_columns