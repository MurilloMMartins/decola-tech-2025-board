package decola.tech.board.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import decola.tech.board.persistence.entity.BoardColumnEntity;
import decola.tech.board.persistence.entity.BoardColumnTypeEnum;
import decola.tech.board.persistence.entity.BoardEntity;
import decola.tech.board.service.BoardQueryService;
import decola.tech.board.service.BoardService;

import static decola.tech.board.persistence.config.ConnectionConfig.getConnection;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException {
        System.out.println("Welcome to the board manager, please select an action:");
        var option = -1;
        while (true) {
            System.out.println("1 - Create a new board.");
            System.out.println("2 - Select an existing board.");
            System.out.println("3 - Remove a board.");
            System.out.println("4 - Exit.");

            option = scanner.nextInt();

            switch (option) {
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.exit(0);

                default -> System.out.println("Invalid action, please select one of the actions from the menu!");
            }
        }
    }

    private void createBoard() throws SQLException {
        var entity = new BoardEntity();
        System.out.println("Enter the name of your new board:");
        entity.setName(scanner.next());

        System.out.println(
                "Will your board have more columns than the default 3? If yes, please enter how many more, if not, type 0:");
        var additionalColumns = scanner.nextInt();
        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.println("Please type the name of the first column of your board:");
        var firstColumnName = scanner.next();
        var firstColumn = createColumn(firstColumnName, BoardColumnTypeEnum.TODO, 0);
        columns.add(firstColumn);

        for (int i = 0; i < additionalColumns; i++) {
            System.out.println("Please type the name of the next 'in progress' column:");
            var nextColumnName = scanner.next();
            var nextColumn = createColumn(nextColumnName, BoardColumnTypeEnum.INPROGRESS, i + 1);
            columns.add(nextColumn);
        }

        System.out.println("Please type the name of the final column:");
        var finalColumnName = scanner.next();
        var finalColumn = createColumn(finalColumnName, BoardColumnTypeEnum.COMPLETED, additionalColumns + 1);
        columns.add(finalColumn);

        System.out.println("Please type the name of the canceled column:");
        var canceledColumnName = scanner.next();
        var canceledColumn = createColumn(canceledColumnName, BoardColumnTypeEnum.CANCELED, additionalColumns + 2);
        columns.add(canceledColumn);

        entity.setBoardColumns(columns);
        try (var connection = getConnection()) {
            var service = new BoardService(connection);
            service.insert(entity);
        }
    }

    private void selectBoard() throws SQLException {
        System.out.println("Enter the id of the board that you want to select:");
        var id = scanner.nextLong();
        try (var connection = getConnection()) {
            var queryService = new BoardQueryService(connection);
            var optional = queryService.findById(id);
            optional.ifPresentOrElse(
                    b -> new BoardMenu(b).execute(),
                    () -> System.out.printf("Board with id %s was not found\n", id)
            );
        }
    }

    private void deleteBoard() throws SQLException {
        System.out.println("Enter the id of the board that will be deleted:");
        var id = scanner.nextLong();
        try (var connection = getConnection()) {
            var service = new BoardService(connection);
            if (service.delete(id)) {
                System.out.printf("Board %s was sucessfully deleted.\n", id);
            } else {
                System.out.printf("Board with id %s was not found\n", id);
            }
        }
    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnTypeEnum type, final int order) {
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setType(type);
        boardColumn.setOrder(order);

        return boardColumn;
    }
}
