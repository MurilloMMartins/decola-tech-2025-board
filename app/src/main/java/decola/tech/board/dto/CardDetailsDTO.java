package decola.tech.board.dto;

import java.time.OffsetDateTime;

public record CardDetailsDTO(
        Long id,
        String title,
        String description,
        boolean locked,
        OffsetDateTime lockedAt,
        String lockReason,
        int lockAmount,
        Long columnId,
        String columnName) {

}
