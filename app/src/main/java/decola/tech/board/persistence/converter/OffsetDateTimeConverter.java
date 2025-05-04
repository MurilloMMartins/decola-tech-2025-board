package decola.tech.board.persistence.converter;

import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class OffsetDateTimeConverter {
    
    public static OffsetDateTime toOffsetDateTime(final Timestamp value) {
        return nonNull(value) ? OffsetDateTime.ofInstant(value.toInstant(), ZoneOffset.UTC) : null;
    }

}
