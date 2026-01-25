package we.employ.you.exception;

/**
 * This custom <code>Exception</code> was designed to notify the user if there
 * were validation errors detected at the data level.
 */
public class ValidationException extends Exception {
    
	private static final long serialVersionUID = 795018938924691155L;

	/**
     * Constructs a new exception with the specified detail message.
     * @param message the message describing the error
     */
    public ValidationException(String message) {
        super(message);
    }
}
