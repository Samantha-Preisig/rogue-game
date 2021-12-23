package rogue;

public class InvalidMoveException extends Exception {

    /**
     * Default constructor.
     */
    public InvalidMoveException() {
        super();
    }

    /**
     * Constructor that takes in an error message to use as part of catching the error.
     * @param message (String) custom error message
     */
    public InvalidMoveException(String message) {
        super(message);
    }

}
