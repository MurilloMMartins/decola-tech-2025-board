package decola.tech.board.persistence.entity;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class LockEntity {

    private Long id;
    private OffsetDateTime lockedAt;
    private String lockReason;
    private OffsetDateTime unlockedAt;
    private String unlockReason;

}
