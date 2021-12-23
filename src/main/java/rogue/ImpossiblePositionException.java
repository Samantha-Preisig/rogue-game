package rogue;

public class ImpossiblePositionException extends Exception {

    /**
     * Default constructor.
     */
    public ImpossiblePositionException() {
        super();
    }

    /**
     * Constructor that takes in an error message to use as part of catching the error.
     * @param message (String) custom error message
     */
    public ImpossiblePositionException(String message) {
        super(message);
    }
}
