package decola.tech.board.persistence.dao;

import static decola.tech.board.persistence.converter.OffsetDateTimeConverter.toOffsetDateTime;
import static java.util.Objects.nonNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import com.mysql.cj.jdbc.StatementImpl;

import decola.tech.board.dto.CardDetailsDTO;
import decola.tech.board.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CardDAO {

    private final Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException {
        var sql = "INSERT INTO cards (title, description, board_column_id) VALUES (?, ?, ?);";
        try (var statement = connection.prepareStatement(sql)) {
            var i = 1;
            statement.setString(i++, entity.getTitle());
            statement.setString(i++, entity.getDescription());
            statement.setLong(i++, entity.getBoardColumn().getId());

            statement.executeUpdate();
            if (statement instanceof StatementImpl impl) {
                entity.setId(impl.getLastInsertID());
            }
        }

        return entity;
    }

    public void moveToColumn(final Long columnId, final Long cardId) throws SQLException {
        var sql = "UPDATE cards SET board_column_id = ? WHERE id = ?;";
        try (var statement = connection.prepareStatement(sql)) {
            var i = 1;
            statement.setLong(i++, columnId);
            statement.setLong(i++, cardId);
            statement.executeUpdate();
        }
    }

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
                             WHERE sub_l.card_id = c.id) locks_amount
                FROM cards c
                LEFT JOIN locks l
                    ON c.id = l.card_id
                    AND l.unlocked_at IS NULL
                INNER JOIN boards_columns bc
                    ON bc.id = c.board_column_id
                WHERE c.id = ?;
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
                        resultSet.getString("bc.name"));
                return Optional.of(dto);
            }
        }
        return Optional.empty();
    }

}
