package todo.app.exception;

public class UserNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -4088353322105992907L;

	public UserNotFoundException(String message) {
		super(message);
	}

	public UserNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
