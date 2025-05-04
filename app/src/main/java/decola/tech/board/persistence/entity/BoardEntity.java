package decola.tech.board.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class BoardEntity {

    private Long id;
    private String name;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<BoardColumnEntity> boardColumns = new ArrayList<>();

    public BoardColumnEntity getFirstColumn() {
        return boardColumns.stream()
                .filter(bc -> bc.getType().equals(BoardColumnTypeEnum.TODO))
                .findFirst().orElseThrow();
    }
}
