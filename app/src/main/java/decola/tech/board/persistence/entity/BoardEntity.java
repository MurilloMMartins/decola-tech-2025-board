package decola.tech.board.persistence.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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
        return getFilteredColumn(bc -> bc.getType().equals(BoardColumnTypeEnum.TODO));
    }

    public BoardColumnEntity getCancelColumn() {
        return getFilteredColumn(bc -> bc.getType().equals(BoardColumnTypeEnum.CANCELED));
    }

    private BoardColumnEntity getFilteredColumn(Predicate<BoardColumnEntity> filter) {
        return boardColumns.stream()
                .filter(filter)
                .findFirst().orElseThrow();
    }
}
