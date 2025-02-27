package todo.app.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import todo.app.logic.TaskDTO;
import todo.app.service.TaskService;
import todo.app.service.UserService;

@RestController
public class TaskController {
	
	private TaskService taskService;
	private UserService userService;
	
	public TaskController(TaskService taskService, UserService userService) {
		this.taskService = taskService;
		this.userService = userService;
	}
	
	@GetMapping("/todo/app/tasks/{taskId}")
	public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long taskId, Authentication authentication) {
		Long userId = getUserId(authentication);
		TaskDTO taskDTO = taskService.getTaskById(taskId, userId);

		return ResponseEntity.ok(taskDTO);
	}
	
	@GetMapping("/todo/app/tasks/")
	public ResponseEntity<List<TaskDTO>> getAllTasks(Authentication authentication) {
		Long userId = getUserId(authentication);
		List<TaskDTO> tasksDTO = taskService.getAllTasks(userId);
		
		return ResponseEntity.ok(tasksDTO);
	}
	
	@PostMapping("/todo/app/tasks/")
	public ResponseEntity<Void> createTask(@RequestBody TaskDTO taskDTO, Authentication authentication) {
		// Get user ID
		Long userId = getUserId(authentication);
		
		// Create location header
		URI taskLocationUri = createLocationHeaderForNewTask(userId); 
		
		// Save new task
		taskService.saveTask(taskDTO, userId);
		
		// Return ResponseEntity with Location header and 201 status
		return ResponseEntity.created(taskLocationUri).build();
	}
	
	@PutMapping("/todo/app/tasks/{taskId}")
	public ResponseEntity<Void> updateTaskById(@RequestBody TaskDTO taskDTO, @PathVariable Long taskId, Authentication authentication) {
		Long userId = getUserId(authentication);
		taskService.updateTask(taskId, userId, taskDTO);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/todo/app/tasks/{taskId}")
	public ResponseEntity<Void> deleteTaskById(@PathVariable Long taskId, Authentication authentication) {
		Long userId = getUserId(authentication);
		taskService.deleteTaskById(taskId, userId);
		return ResponseEntity.noContent().build();
	}
	

	private URI createLocationHeaderForNewTask(Long userId) {
		Long newTaskId = taskService.getNextTaskIdForUser(userId);
		
		URI locationUri = ServletUriComponentsBuilder
				.fromCurrentContextPath()
				.path("/todo/app/tasks/{taskId}")
				.buildAndExpand(newTaskId)
				.toUri();
		
		return locationUri;
	}

	private Long getUserId(Authentication authentication) {
		String principalUsername = authentication.getName();
		return userService.getUserIdByUsername(principalUsername);
	}
}
