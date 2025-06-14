/**
 * 
 */
package todo.app.exception;

/**
 * 
 */
public class InvalidTaskDataException extends RuntimeException {

	private static final long serialVersionUID = -8453906735224358407L;

	/**
	 * @param message
	 */
	public InvalidTaskDataException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidTaskDataException(String message, Throwable cause) {
		super(message, cause);
	}
}
