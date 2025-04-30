package decola.tech.board.dto;

import java.time.OffsetDateTime;

public record CardDetails(
        Long id,
        boolean locked,
        OffsetDateTime lockedAt,
        String lockReason,
        int lockAmount,
        Long columnId,
        String columnName) {

}
