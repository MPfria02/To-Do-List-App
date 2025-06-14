package todo.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GoblalExceptionHandler { 
	
	@ExceptionHandler(InvalidTaskDataException.class)
	public ResponseEntity<String> handleInvalidTaskDataException(InvalidTaskDataException exception) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
	}
	
	@ExceptionHandler(TaskNotFoundException.class)
	public ResponseEntity<String> handleTaskNotFoundException(TaskNotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
	}
	
	@ExceptionHandler(InvalidUserDataException.class)
	public ResponseEntity<String> handleInvalidUserDataException(InvalidUserDataException exception) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> handleIUserNotFoundException(UserNotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
	}	
}
