package decola.tech.board.persistence.dao;

import static java.util.Objects.nonNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import decola.tech.board.dto.CardDetailsDTO;
import lombok.AllArgsConstructor;

import static decola.tech.board.persistence.converter.OffsetDateTimeConverter.toOffsetDateTime;

@AllArgsConstructor
public class CardDAO {

    private final Connection connection;

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        var sql = """
                SELECT c.id,
                       c.title,
                       c.description,
                       l.locked_at,
                       l.lock_reason,
                       c.board_column_id,
                       bc.name,
                       (SELECT COUNT(sub_l.id)
                             FROM locks sub_l
                             WHERE sub_l.id = c.id) locks_amount
                FROM cards c
                LEFT JOIN locks l
                    ON c.id = l.card_id
                    AND l.unlocked_at IS NULL
                INNER JOIN boards_columns bc
                    ON bc.id = c.board_column_id
                WHERE id = ?;
                """;

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeQuery();

            var resultSet = statement.getResultSet();
            if (resultSet.next()) {
                var dto = new CardDetailsDTO(
                    resultSet.getLong("c.id"),
                    resultSet.getString("c.title"),
                    resultSet.getString("c.description"),
                    nonNull(resultSet.getString("l.lock_reason")),
                    toOffsetDateTime(resultSet.getTimestamp("l.locked_at")),
                    resultSet.getString("l.lock_reason"),
                    resultSet.getInt("locks_amount"),
                    resultSet.getLong("c.board_column_id"),
                    resultSet.getString("bc.name")
                );
                return Optional.of(dto);
            }
        }
        return null;
    }

}
