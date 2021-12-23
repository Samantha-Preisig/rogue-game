package rogue;

public class NotEnoughDoorsException extends Exception {

    /**
     * Default constructor.
     */
    public NotEnoughDoorsException() {
        super();
    }

    /**
     * Constructor that takes in an error message to use as part of catching the error.
     * @param message (String) custom error message
     */
    public NotEnoughDoorsException(String message) {
        super(message);
    }
}
