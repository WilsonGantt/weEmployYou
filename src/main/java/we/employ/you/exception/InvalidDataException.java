package we.employ.you.exception;

import java.io.Serial;

/**
 * This custom <code>Exception</code> is thrown if the data in the database is in
 * an invalid state, an example being when multiple rows of data are returned 
 * when only one is expected.
 */
public class InvalidDataException extends RuntimeException {
    
	@Serial
    private static final long serialVersionUID = -6249433169500678240L;

	/**
     * Constructs a new exception with the specified detail message.
     * @param message the message describing the error
     */
    public InvalidDataException(String message) {
        super(message);
    }
}

