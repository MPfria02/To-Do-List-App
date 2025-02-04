package todo.app.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import todo.app.config.SystemTestConfig;
import todo.app.logic.Task;
import todo.app.security.SecurityConfig;
import todo.app.service.TaskService;
import todo.app.service.UserService;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(TaskController.class)
@Import({SecurityConfig.class, SystemTestConfig.class})
class TaskControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@SuppressWarnings("removal")
	@MockBean
	private UserService userService;
	
	@SuppressWarnings("removal")
	@MockBean
	private TaskService taskService;

	@Test
	@WithMockUser(roles = {"USER"})
	void shouldReturnTaskWhenIdIsValid() throws Exception {
		// Arrange
		Long userId = 1L, taskId = 1L; 
		String title = "MockTitle", description = "MockDescription";
		Task task = new Task(title, description);
		given(taskService.getTaskById(taskId, userId)).willReturn(task);
		given(userService.getUserIdByUsername(any(String.class))).willReturn(userId);
		
		// Act & Assert
		mockMvc.perform(get("/todo/app/tasks/1"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.title").value("MockTitle"))
			.andExpect(jsonPath("$.description").value("MockDescription"));
		
		// Verify
		verify(taskService).getTaskById(taskId, userId);	
	}

	@Test
	@WithMockUser(roles = {"USER"})
	void shouldReturnNotFoundWhenAccessInvalidTaskId() throws Exception {
		// Arrange
		Long userId = 1L, taskId = 6L;
		when(userService.getUserIdByUsername(any(String.class))).thenReturn(userId);
		given(taskService.getTaskById(taskId, userId)).willThrow(new IllegalArgumentException("Invalid task ID"));
		
		// Act & Assert
		mockMvc.perform(get("/todo/app/tasks/" + taskId))
			.andExpect(status().isNotFound());
		
		// Verify
		verify(taskService).getTaskById(taskId, userId);
	}

	@Test
	@WithMockUser(roles = {"USER"})
	void shouldReturnAllTasksWhenUserIsAuthenticated() throws Exception {
		// Arrange 
		Long userId = 1L;
		List<Task> tasks = Arrays.asList(new Task("MockTitle", "MockDescription"));
		when(userService.getUserIdByUsername(any(String.class))).thenReturn(userId);
		given(taskService.getAllTasks(userId)).willReturn(tasks);
		
		// Act & Assert
		mockMvc.perform(get("/todo/app/tasks"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$[0].title").value("MockTitle"))
			.andExpect(jsonPath("$[0].description").value("MockDescription"));
		
		// Verify
		verify(taskService).getAllTasks(userId);
	}

	@Test
	@WithAnonymousUser
	void shouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
		// Act & Assert
		mockMvc.perform(get("/todo/app/tasks"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(roles = {"USER"})
	void shouldUpdateTaskWhenUserIsAuthorizedAndTaskIdIsValid() throws Exception {
		
		// Arrange
		Long userId = 1L, taskId = 1L;
		when(userService.getUserIdByUsername(any(String.class))).thenReturn(userId);
		doNothing().when(taskService).updateTask(eq(taskId), eq(userId), any(Task.class));
		
		// Act & Assert
		mockMvc.perform(put("/todo/app/tasks/" + taskId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(createMockTaskJson()))
		.andExpect(status().isNoContent());
		
		// Verify
		verify(taskService).updateTask(eq(taskId), eq(userId), any(Task.class));
	}
	
	@Test
	@WithMockUser(roles = {"USER"})
	void shouldCreateTaskWhenUserIsAuthenticatedAndTaskIsValid() throws Exception {
		// Arrange
		Long userId = 1L, newTaskId = 3L;
		when(userService.getUserIdByUsername(any(String.class))).thenReturn(userId);
		doNothing().when(taskService).saveTask(any(Task.class), eq(userId));
		when(taskService.getNextTaskIdForUser(userId)).thenReturn(newTaskId);
		
		// Act & Assert
		mockMvc.perform(post("/todo/app/tasks")
				.contentType(MediaType.APPLICATION_JSON)
				.content(createMockTaskJson()))
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", "http://localhost/todo/app/tasks/" + newTaskId));
		
		// Verify
		verify(taskService).saveTask(any(Task.class), eq(userId));
	}
	
	@Test
	@WithMockUser(roles = {"USER"})
	void shouldDeleteTaskWhenUserIsAuthenticatedAndTaskIdIsValid() throws Exception {
		// Arrange
		Long userId = 1L, taskId = 1L;
		when(userService.getUserIdByUsername(any(String.class))).thenReturn(userId);
		when(taskService.deleteTaskById(taskId, userId)).thenReturn(any(Task.class));
		
		// Act & Assert
		mockMvc.perform(delete("/todo/app/tasks/" + taskId))
			.andExpect(status().isNoContent());
		
		// Verify
		verify(taskService).deleteTaskById(taskId, userId);
	}

	private String createMockTaskJson() {
		String mockUserJson = """
            {
                "title": "Title",
                "description": "Description"
            }
            """;
		
		return mockUserJson;
	}
}
