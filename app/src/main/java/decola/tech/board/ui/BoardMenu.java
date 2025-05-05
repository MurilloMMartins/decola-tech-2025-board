package decola.tech.board.ui;

import static decola.tech.board.persistence.config.ConnectionConfig.getConnection;

import java.sql.SQLException;
import java.util.Scanner;

import decola.tech.board.dto.BoardColumnInfoDTO;
import decola.tech.board.persistence.entity.BoardColumnEntity;
import decola.tech.board.persistence.entity.BoardEntity;
import decola.tech.board.persistence.entity.CardEntity;
import decola.tech.board.service.BoardColumnQueryService;
import decola.tech.board.service.BoardQueryService;
import decola.tech.board.service.CardQueryService;
import decola.tech.board.service.CardService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    private final BoardEntity entity;

    public void execute() {
        try {
            System.out.printf("Welcome to board %s, please select an action:\n", entity.getId());
            var option = -1;
            while (option != 9) {
                System.out.println("1 - Create a card.");
                System.out.println("2 - Move a card.");
                System.out.println("3 - Lock a card.");
                System.out.println("4 - Unlock a card.");
                System.out.println("5 - Cancel a card.");
                System.out.println("6 - Print board.");
                System.out.println("7 - Print column with cards.");
                System.out.println("8 - Print card.");
                System.out.println("9 - Return to previous menu.");
                System.out.println("10 - Exit.");

                option = scanner.nextInt();

                switch (option) {
                    case 1 -> createCard();
                    case 2 -> moveCardToNextColumn();
                    case 3 -> lockCard();
                    case 4 -> unlockCard();
                    case 5 -> cancelCard();
                    case 6 -> showBoard();
                    case 7 -> showColumn();
                    case 8 -> showCard();
                    case 9 -> System.out.println("Returning to previous menu.");
                    case 10 -> System.exit(0);

                    default -> System.out.println("Invalid action, please select one of the actions from the menu!");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    private void createCard() throws SQLException {
        var card = new CardEntity();
        System.out.println("Informe o título do card:");
        card.setTitle(scanner.next());
        System.out.println("Informe a descrição do card:");
        card.setDescription(scanner.next());
        card.setBoardColumn(entity.getFirstColumn());

        try (var connection = getConnection()) {
            new CardService(connection).insert(card);
        }
    }

    private void moveCardToNextColumn() throws SQLException {
        System.out.println("Informe o id do card que deseja mover para a próxima coluna:");
        var cardId = scanner.nextLong();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getType()))
                .toList();
        try (var connection = getConnection()) {
            new CardService(connection).moveToNextColumn(cardId, boardColumnsInfo);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void lockCard() throws SQLException {
        System.out.println("Informe o id do card que será bloqueado:");
        var cardId = scanner.nextLong();
        System.out.println("Informe o motivo do bloqueio do card:");
        var reason = scanner.next();
        
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getType()))
                .toList();

        try(var connection = getConnection()) {
            new CardService(connection).lock(cardId, reason, boardColumnsInfo);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private Object unlockCard() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'unlockCard'");
    }

    private void cancelCard() throws SQLException {
        System.out.println("Informe o id do card que deseja mover para a coluna de cancelamento");
        var cardId = scanner.nextLong();
        var canceledColumn = entity.getCancelColumn();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getType()))
                .toList();
        try (var connection = getConnection()) {
            new CardService(connection).cancel(cardId, canceledColumn.getId(), boardColumnsInfo);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void showBoard() throws SQLException {
        try (var connection = getConnection()) {
            var optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.ifPresent(b -> {
                System.out.printf("Board [%s,%s]\n", b.id(), b.name());
                b.columns().forEach(c -> {
                    System.out.printf("Coluna [%s] tipo: [%s], tem %s cards.\n", c.name(), c.type(), c.cardsAmount());
                });
            });
        }
    }

    private void showColumn() throws SQLException {
        var columnsId = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumn = -1L;
        while (!columnsId.contains(selectedColumn)) {
            System.out.printf("Escolha uma coluna do board %s\n", entity.getName());
            entity.getBoardColumns()
                    .forEach(c -> System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getType()));
            selectedColumn = scanner.nextLong();
        }
        try (var connection = getConnection()) {
            var column = new BoardColumnQueryService(connection).findById(selectedColumn);
            column.ifPresent(co -> {
                System.out.printf("Coluna %s tipo %s\n", co.getName(), co.getType());
                co.getCards().forEach(ca -> System.out.printf("Card %s - %s\n Descrição: %s\n", ca.getId(),
                        ca.getTitle(), ca.getDescription()));
            });
        }
    }

    private void showCard() throws SQLException {
        System.out.println("Informe o id do card que deseja visualizar:");
        var selectedCardId = scanner.nextLong();
        try (var connection = getConnection()) {
            new CardQueryService(connection).findById(selectedCardId)
                    .ifPresentOrElse(
                            c -> {
                                System.out.printf("Card %s - %s.\n", c.id(), c.title());
                                System.out.printf("Descrição: %s\n", c.description());
                                System.out.println(c.locked() ? "Está bloqueado. Motivo: " + c.lockReason()
                                        : "Não está bloqueado.");
                                System.out.printf("Já foi bloqueado %s vezes.\n", c.lockAmount());
                                System.out.printf("Está no momento na coluna %s - %s\n", c.columnId(), c.columnName());
                            },
                            () -> System.out.printf("Não existe um card com o id %s\n", selectedCardId));
        }
    }

}
