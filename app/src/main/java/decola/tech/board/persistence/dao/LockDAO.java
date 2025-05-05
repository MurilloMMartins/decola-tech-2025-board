package decola.tech.board.persistence.dao;

import static decola.tech.board.persistence.converter.OffsetDateTimeConverter.toTimestamp;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LockDAO {

    private final Connection connection;

    public void lock(final Long cardId, final String reason) throws SQLException {
        var sql = "INSERT INTO locks (locked_at, lock_reason, card_id) VALUES (?, ?, ?);";
        try (var statement = connection.prepareStatement(sql)) {
            var i = 1;
            statement.setTimestamp(i++, toTimestamp(OffsetDateTime.now()));
            statement.setString(i++, reason);
            statement.setLong(i++, cardId);
            statement.executeUpdate();
        }
    }
}
