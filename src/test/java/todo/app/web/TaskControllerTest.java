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
import todo.app.exception.InvalidTaskDataException;
import todo.app.exception.TaskNotFoundException;
import todo.app.logic.Task;
import todo.app.security.SecurityConfig;
import todo.app.service.TaskService;
import todo.app.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;

import javax.sound.midi.VoiceStatus;

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
	
	private static final String TASKS_URL = "/todo/app/tasks/";

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
		mockMvc.perform(get(TASKS_URL + taskId))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.title").value("MockTitle"))
			.andExpect(jsonPath("$.description").value("MockDescription"));
		
		// Verify
		verify(taskService).getTaskById(taskId, userId);	
	}
	
	@Test
	@WithMockUser(roles = {"USER"})
	void shouldReturnBadRequestWhenTaskDataIsNotValid() throws Exception {
		// Arrange
		Long userId = 1L;
		String exceptionExpectedMessage = "Invalid task attributes. Title and description cannot be empty or null.";
		given(userService.getUserIdByUsername(any(String.class))).willReturn(userId);
		willThrow(new InvalidTaskDataException(exceptionExpectedMessage)).given(taskService).saveTask(any(Task.class), eq(userId));
		
		// Act & Assert
		mockMvc.perform(post(TASKS_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(createInvalidMockTaskJson()))
			.andExpect(status().isBadRequest())
			.andExpect(result -> {
			    Throwable ex = result.getResolvedException();
			    assertNotNull(ex);
			    assertTrue(ex instanceof InvalidTaskDataException);
			    assertEquals(exceptionExpectedMessage, ex.getMessage());
			});
		
		// Verify
		verify(taskService).saveTask(any(Task.class), eq(userId));
	}

	@Test
	@WithMockUser(roles = {"USER"})
	void shouldReturnNotFoundWhenAccessInvalidTaskId() throws Exception {
		// Arrange
		Long userId = 1L, taskId = 6L;
		String exceptionExpectedMessage = "Invalid task ID";
		given(userService.getUserIdByUsername(any(String.class))).willReturn(userId);
		given(taskService.getTaskById(taskId, userId)).willThrow(new TaskNotFoundException("Invalid task ID"));
		
		// Act & Assert
		mockMvc.perform(get(TASKS_URL + taskId))
			.andExpect(status().isNotFound())
			.andExpect(result ->  {
			    Throwable ex = result.getResolvedException();
			    assertNotNull(ex);
			    assertTrue(ex instanceof TaskNotFoundException);
			    assertEquals(exceptionExpectedMessage, ex.getMessage());
			});
		
		// Verify
		verify(taskService).getTaskById(taskId, userId);
	}

	@Test
	@WithMockUser(roles = {"USER"})
	void shouldReturnAllTasksWhenUserIsAuthenticated() throws Exception {
		// Arrange 
		Long userId = 1L;
		List<Task> tasks = Arrays.asList(new Task("MockTitle", "MockDescription"));
		given(userService.getUserIdByUsername(any(String.class))).willReturn(userId);
		given(taskService.getAllTasks(userId)).willReturn(tasks);
		
		// Act & Assert
		mockMvc.perform(get(TASKS_URL))
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
		mockMvc.perform(get(TASKS_URL))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(roles = {"USER"})
	void shouldUpdateTaskWhenUserIsAuthorizedAndTaskIdIsValid() throws Exception {
		
		// Arrange
		Long userId = 1L, taskId = 1L;
		given(userService.getUserIdByUsername(any(String.class))).willReturn(userId);
		willDoNothing().given(taskService).updateTask(eq(taskId), eq(userId), any(Task.class));
		
		// Act & Assert
		mockMvc.perform(put(TASKS_URL + taskId)
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
		given(userService.getUserIdByUsername(any(String.class))).willReturn(userId);
		willDoNothing().given(taskService).saveTask(any(Task.class), eq(userId));
		given(taskService.getNextTaskIdForUser(userId)).willReturn(newTaskId);
		
		// Act & Assert
		mockMvc.perform(post(TASKS_URL)
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
		given(userService.getUserIdByUsername(any(String.class))).willReturn(userId);
		given(taskService.deleteTaskById(taskId, userId)).willReturn(new Task("DELETE", "deleted task"));
		
		// Act & Assert
		mockMvc.perform(delete(TASKS_URL + taskId))
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
	
	private String createInvalidMockTaskJson() {
		String mockUserJson = """
            {
                "title": "",
                "description": null
            }
            """;
		
		return mockUserJson;
	}
}
