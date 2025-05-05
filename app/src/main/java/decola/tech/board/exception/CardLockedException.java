package decola.tech.board.exception;

public class CardLockedException extends RuntimeException{

    public CardLockedException(final String message) {
        super(message);
    }
}
