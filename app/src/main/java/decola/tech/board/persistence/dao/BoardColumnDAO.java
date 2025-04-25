package decola.tech.board.persistence.dao;

import static decola.tech.board.persistence.entity.BoardColumnTypeEnum.findByName;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.jdbc.StatementImpl;

import decola.tech.board.persistence.entity.BoardColumnEntity;
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

    public List<BoardColumnEntity> findByBoardId(Long id) throws SQLException {
        List<BoardColumnEntity> entities = new ArrayList<>();
        var sql = "SELECT id, name, `order`, type FROM boards_columns WHERE board_id = ? ORDER BY `order`";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
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

        }
        throw new UnsupportedOperationException("Unimplemented method 'findByBoardId'");
    }
}
