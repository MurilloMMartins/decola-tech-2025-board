package decola.tech.board.persistence.entity;

import java.util.stream.Stream;

public enum BoardColumnTypeEnum {

    TODO, INPROGRESS, COMPLETED, CANCELED;
        
    public static BoardColumnTypeEnum findByName(final String name) {
        return Stream.of(BoardColumnTypeEnum.values())
                .filter(b -> b.name().equals(name))
                .findFirst().orElseThrow();
    }

}
