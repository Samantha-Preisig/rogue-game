package rogue;

public class NoSuchItemException extends Exception {

    /**
     * Default constructor.
     */
    public NoSuchItemException() {
        super();
    }

    /**
     * Constructor that takes in an error message to use as part of catching the error.
     * @param message (String) custom error message
     */
    public NoSuchItemException(String message) {
        super(message);
    }
}
