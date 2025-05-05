package decola.tech.board.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import decola.tech.board.dto.BoardColumnInfoDTO;
import decola.tech.board.exception.CardFinishedException;
import decola.tech.board.exception.CardLockedException;
import decola.tech.board.exception.EntityNotFoundException;
import decola.tech.board.persistence.dao.CardDAO;
import decola.tech.board.persistence.dao.LockDAO;
import decola.tech.board.persistence.entity.BoardColumnTypeEnum;
import decola.tech.board.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CardService {

    private final Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            dao.insert(entity);
            connection.commit();
            return entity;
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void moveToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> boardColumnsInfo)
            throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("Card with id %s was not found.".formatted(cardId)));

            if (dto.locked()) {
                throw new CardLockedException(
                        "Card %s is locked, you need to unlock it to move it.".formatted(cardId));
            }
            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("This card belongs to another board."));
            if (currentColumn.type().equals(BoardColumnTypeEnum.COMPLETED)) {
                throw new CardFinishedException("Card has already been completed.");

            }

            var nextColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst().orElseThrow(() -> new IllegalStateException("This card was cancelled."));
            dao.moveToColumn(nextColumn.id(), cardId);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void cancel(final Long cardId, final Long cancelColumnId, final List<BoardColumnInfoDTO> boardColumnsInfo)
            throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("Card with id %s was not found.".formatted(cardId)));

            if (dto.locked()) {
                throw new CardLockedException(
                        "Card %s is locked, you need to unlock it to move it.".formatted(cardId));
            }
            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("This card belongs to another board."));
            if (currentColumn.type().equals(BoardColumnTypeEnum.COMPLETED)) {
                throw new CardFinishedException("Card has already been completed.");

            }

            boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst().orElseThrow(() -> new IllegalStateException("This card was cancelled."));

            dao.moveToColumn(cancelColumnId, cardId);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void lock(final Long id, final String reason, final List<BoardColumnInfoDTO> boardColumnsInfo)
            throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(id);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("Card with id %s was not found.".formatted(id)));
            if (dto.locked()) {
                throw new CardLockedException(
                        "Card %s is already locked.".formatted(id));
            }

            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow();
            if (currentColumn.type().equals(BoardColumnTypeEnum.COMPLETED)
                    || currentColumn.type().equals(BoardColumnTypeEnum.CANCELED)) {
                throw new IllegalStateException(
                        "This card is in a column of type %s and can't be locked."
                                .formatted(currentColumn.type()));
            }

            var lockDAO = new LockDAO(connection);
            lockDAO.lock(id, reason);

            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void unlock(final Long id, final String reason) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(id);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("Card with id %s was not found.".formatted(id)));
            if (!dto.locked()) {
                throw new CardLockedException(
                        "Card %s is not locked.".formatted(id));
            }

            var lockDAO = new LockDAO(connection);
            lockDAO.unlock(id, reason);

            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }
}
