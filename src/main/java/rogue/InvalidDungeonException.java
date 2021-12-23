package rogue;

public class InvalidDungeonException extends Exception {

    /**
     * Default constructor.
     */
    public InvalidDungeonException() {
        super();
    }

    /**
     * Constructor that takes in an error message to use as part of catching the error.
     * @param message (String) custom error message
     */
    public InvalidDungeonException(String message) {
        super(message);
    }
}
