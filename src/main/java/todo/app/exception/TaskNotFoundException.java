package todo.app.exception;

public class TaskNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -7058484588835994070L;

	public TaskNotFoundException(String message) {
		super(message);
	}

	public TaskNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
