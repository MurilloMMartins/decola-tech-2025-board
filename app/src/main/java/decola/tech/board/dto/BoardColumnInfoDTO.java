package decola.tech.board.dto;

import decola.tech.board.persistence.entity.BoardColumnTypeEnum;

public record BoardColumnInfoDTO(Long id, int order, BoardColumnTypeEnum type) {

}
