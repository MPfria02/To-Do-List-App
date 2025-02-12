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

import todo.app.logic.Task;
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
	public ResponseEntity<Task> getTaskById(@PathVariable Long taskId, Authentication authentication) {
		Long userId = getUserId(authentication);
		Task task = taskService.getTaskById(taskId, userId);

		return ResponseEntity.ok(task);
	}
	
	@GetMapping("/todo/app/tasks/")
	public ResponseEntity<List<Task>> getAllTasks(Authentication authentication) {
		Long userId = getUserId(authentication);
		List<Task> tasks = taskService.getAllTasks(userId);
		
		return ResponseEntity.ok(tasks);
	}
	
	@PostMapping("/todo/app/tasks/")
	public ResponseEntity<Void> createTask(@RequestBody Task task, Authentication authentication) {
		// Save new task
		Long userId = getUserId(authentication);
		taskService.saveTask(task, userId);
		
		// Create location header
		URI taskLocationUri = createLocationHeaderForNewTask(task, userId); 
		
		// Return ResponseEntity with Location header and 201 status
		return ResponseEntity.created(taskLocationUri).build();
	}
	
	@PutMapping("/todo/app/tasks/{taskId}")
	public ResponseEntity<Void> updateTaskById(@RequestBody Task task, @PathVariable Long taskId, Authentication authentication) {
		Long userId = getUserId(authentication);
		taskService.updateTask(taskId, userId, task);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/todo/app/tasks/{taskId}")
	public ResponseEntity<Void> deleteTaskById(@PathVariable Long taskId, Authentication authentication) {
		Long userId = getUserId(authentication);
		taskService.deleteTaskById(taskId, userId);
		return ResponseEntity.noContent().build();
	}
	

	private URI createLocationHeaderForNewTask(Task task, Long userId) {
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
