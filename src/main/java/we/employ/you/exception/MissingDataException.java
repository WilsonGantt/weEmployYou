package we.employ.you.exception;

/**
 * This custom <code>Exception</code> was designed to notify the user if there
 * is any critical data missing from the database.
 */
public class MissingDataException extends Exception {
    
	private static final long serialVersionUID = -3914417528181095918L;

	/**
     * Constructs a new exception with the specified detail message.
     * @param message the message describing the error
     */
    public MissingDataException(String message) {
        super(message);
    }
}