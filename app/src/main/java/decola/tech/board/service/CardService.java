package decola.tech.board.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import decola.tech.board.dto.BoardColumnInfoDTO;
import decola.tech.board.dto.CardDetailsDTO;
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
                    () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId)));

            if (dto.locked()) {
                throw new CardLockedException(
                        "O card %s está bloqueado, é necessário desbloquea-lo para mover".formatted(cardId));
            }
            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board."));
            if (currentColumn.type().equals(BoardColumnTypeEnum.COMPLETED)) {
                throw new CardFinishedException("O card já foi finalizado.");

            }

            var nextColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst().orElseThrow(() -> new IllegalStateException("O card está cancelado."));
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
                    () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId)));

            if (dto.locked()) {
                throw new CardLockedException(
                        "O card %s está bloqueado, é necessário desbloquea-lo para mover".formatted(cardId));
            }
            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board."));
            if (currentColumn.type().equals(BoardColumnTypeEnum.COMPLETED)) {
                throw new CardFinishedException("O card já foi finalizado.");

            }

            boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst().orElseThrow(() -> new IllegalStateException("O card está cancelado."));

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
                    () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(id)));
            if (dto.locked()) {
                throw new CardLockedException(
                        "O card %s já está bloqueado.".formatted(id));
            }

            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow();
            if (currentColumn.type().equals(BoardColumnTypeEnum.COMPLETED)
                    || currentColumn.type().equals(BoardColumnTypeEnum.CANCELED)) {
                throw new IllegalStateException(
                        "O card está em uma coluna do tipo %s e não pode ser bloqueado".formatted(currentColumn.type()));
            }

            var lockDAO = new LockDAO(connection);
            lockDAO.lock(id, reason);

            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }
}
