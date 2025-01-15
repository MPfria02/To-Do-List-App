package todo.app.web;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import todo.app.logic.User;
import todo.app.service.UserService;

@RestController
public class AuthController {
	
	private UserService userService;
	
	public AuthController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping("/todo/app/register")
	public ResponseEntity<Void> registerUser(@RequestBody User user) {
		
		// Create a new user
		userService.saveUser(user);
		
		// Build Location header
		URI location = createLocationHeaderForNewUser(user);
				
		// Return ResponseEntity with Location header and 201 status
		return ResponseEntity.created(location).build();
	}

	private URI createLocationHeaderForNewUser(User user) {
		// Get new user id
		Long newUserId = userService.getUserIdByUsername(user.getUsername());
		
		// Return Location header
		return ServletUriComponentsBuilder
				.fromCurrentContextPath()
				.path("/todo/app/users/{userId}")
				.buildAndExpand(newUserId)
				.toUri();
	}
}
