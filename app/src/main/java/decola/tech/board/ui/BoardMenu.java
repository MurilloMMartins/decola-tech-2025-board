package decola.tech.board.ui;

import java.util.Scanner;

import decola.tech.board.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner scanner = new Scanner(System.in);

    private final BoardEntity entity;

    public void execute() {
        System.out.printf("Welcome to board %s, please select an action:", entity.getId());
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
                case 3 -> unlockCard();
                case 4 -> unlockCard();
                case 5 -> cancelCard();
                case 6 -> showCard();
                case 7 -> showCard();
                case 8 -> showCard();
                case 9 -> System.out.println("Returning to previous menu.");
                case 10 -> System.exit(0);

                default -> System.out.println("Invalid action, please select one of the actions from the menu!");
            }
        }
    }

    private Object createCard() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createCard'");
    }

    private Object moveCardToNextColumn() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'moveCardToNextColumn'");
    }

    private Object unlockCard() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'unlockCard'");
    }

    private Object cancelCard() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cancelCard'");
    }

    private Object showCard() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'showCard'");
    }

}
