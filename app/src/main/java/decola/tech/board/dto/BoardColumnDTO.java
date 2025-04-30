package decola.tech.board.dto;

import decola.tech.board.persistence.entity.BoardColumnTypeEnum;

public record BoardColumnDTO(Long id, String name, BoardColumnTypeEnum type, int cardsAmount) {

}
