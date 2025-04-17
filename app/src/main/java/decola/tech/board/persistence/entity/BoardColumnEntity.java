package decola.tech.board.persistence.entity;

import lombok.Data;

@Data
public class BoardColumnEntity {
    
    private Long id;
    private String name;
    private int order;
    private BoardColumnTypeEnum type;
    private BoardEntity board = new BoardEntity();

}
