package todo.app.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import todo.app.logic.UserDTO;
import todo.app.service.UserService;

@RestController
public class UserController {

	private UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/todo/app/users/{userId}")
	public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
		UserDTO userDTO = userService.getUserById(userId);
		return ResponseEntity.ok(userDTO);
	}
	
	@GetMapping("/todo/app/users/")
	public ResponseEntity<List<UserDTO>> getAllUsers() {
		List<UserDTO> usersDTO = userService.getAllUsers();
		return ResponseEntity.ok(usersDTO);
	}
	
	@DeleteMapping("/todo/app/users/{userId}") 
	public ResponseEntity<Void> deleteUserById(@PathVariable Long userId) {
		userService.deleteUserById(userId);
		return ResponseEntity.noContent().build();
	}
}
