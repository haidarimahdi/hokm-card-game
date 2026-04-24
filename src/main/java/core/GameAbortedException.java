package core;

public class GameAbortedException extends RuntimeException {
    public GameAbortedException(String message) {
        super(message);
    }
}
