package todo.app.exception;

public class InvalidUserDataException extends RuntimeException {

	private static final long serialVersionUID = 2088553943182060509L;

	public InvalidUserDataException(String message) {
		super(message);
	}

	public InvalidUserDataException(String message, Throwable cause) {
		super(message, cause);
	}
}
