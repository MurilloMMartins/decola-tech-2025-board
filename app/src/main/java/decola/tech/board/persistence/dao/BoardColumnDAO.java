package decola.tech.board.persistence.dao;

import static decola.tech.board.persistence.entity.BoardColumnTypeEnum.findByName;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mysql.cj.jdbc.StatementImpl;

import decola.tech.board.dto.BoardColumnDTO;
import decola.tech.board.persistence.entity.BoardColumnEntity;
import decola.tech.board.persistence.entity.CardEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BoardColumnDAO {

    private final Connection connection;

    public BoardColumnEntity insert(final BoardColumnEntity entity) throws SQLException {
        var sql = "INSERT INTO boards_columns (name, `order`, type, board_id) VALUES (?, ?, ?, ?);";
        try (var statement = connection.prepareStatement(sql)) {
            var i = 1;
            statement.setString(i++, entity.getName());
            statement.setInt(i++, entity.getOrder());
            statement.setString(i++, entity.getType().name());
            statement.setLong(i, entity.getBoard().getId());
            statement.executeUpdate();

            if (statement instanceof StatementImpl impl) {
                entity.setId(impl.getLastInsertID());
            }

            return entity;
        }
    }

    public List<BoardColumnEntity> findByBoardId(Long boardId) throws SQLException {
        List<BoardColumnEntity> entities = new ArrayList<>();
        var sql = "SELECT id, name, `order`, type FROM boards_columns WHERE board_id = ? ORDER BY `order`";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while (resultSet.next()) {
                var entity = new BoardColumnEntity();
                entity.setId(resultSet.getLong("id"));
                entity.setName(resultSet.getString("name"));
                entity.setOrder(resultSet.getInt("order"));
                entity.setType(findByName(resultSet.getString("type")));
                entities.add(entity);
            }
            return entities;
        }
    }

    public List<BoardColumnDTO> findByBoardIdWithDetails(Long boardId) throws SQLException {
        List<BoardColumnDTO> dtos = new ArrayList<>();
        var sql = """
                    SELECT  bc.id,
                            bc.name,
                            bc.type,
                            COUNT(SELECT c.id FROM cards c WHERE c.board_column_id = bc.id) cards_amount
                        FROM boards_columns bc
                        WHERE board_id = ?
                        ORDER BY `order`
                """;
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while (resultSet.next()) {
                var dto = new BoardColumnDTO(
                        resultSet.getLong("bc.id"),
                        resultSet.getString("bc.name"),
                        findByName(resultSet.getString("bc.type")),
                        resultSet.getInt("cards_amount"));
                dtos.add(dto);
            }
            return dtos;
        }
    }

    public Optional<BoardColumnEntity> findById(Long boardId) throws SQLException {
        var sql = """
                    SELECT  bc.name,
                            bc.type,
                            c.id,
                            c.title,
                            c.description
                        FROM boards_columns bc
                        INNER JOIN cards c
                            ON c.board_column_id = bc.id
                        WHERE bc.id = ?
                """;
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()) {
                var entity = new BoardColumnEntity();
                entity.setName(resultSet.getString("bc.name"));
                entity.setType(findByName(resultSet.getString("bc.type")));
                do {
                    var card = new CardEntity();
                    card.setId(resultSet.getLong("c.id"));
                    card.setTitle(resultSet.getString("c.title"));
                    card.setDescription(resultSet.getString("c.description"));
                    entity.getCards().add(card);
                } while (resultSet.next());
            }
            return Optional.empty();
        }
    }
}
