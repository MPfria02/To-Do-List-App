package todo.app.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.http.HttpHeaders;

import todo.app.ToDoListApplication;
import todo.app.config.SystemTestConfig;
import todo.app.logic.Task;

@SpringBootTest(classes = {ToDoListApplication.class}, 
				webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(SystemTestConfig.class)
@TestPropertySource(locations = "classpath:todo/testdb/application-test.properties")
class TaskClientTest {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	private static final String TASKS_URL = "/todo/app/tasks/";
	private String username, password;
	@Autowired
	private TestRestTemplate restTemplate;
	
	@BeforeEach
	void resetDatabaseData() {
		jdbcTemplate.execute("TRUNCATE TABLE t_tasks");
		jdbcTemplate.execute("INSERT INTO t_tasks (id, title, description, user_id) VALUES\r\n"
				+ "(1,'Buy groceries', 'Milk, eggs, bread', 1),\r\n"
				+ "(1,'Finish project', 'Complete the final draft by Friday', 2),\r\n"
				+ "(2,'Book tickets', 'Vacation tickets to Hawaii', 1),\r\n"
				+ "(1,'Pay bills', 'Electricity and water bills', 3);");
		
		System.out.println("Table t_tasks has been successfully reseted");
	}

	@Test
	void shouldReturnTaskWhenTaskIdIsValidAndUserIsAuthenticated() {
		// Arrange
		Long taskId = 1L;
		username = "Alice"; password = "password123";
		String titleExpected = "Buy groceries", descriptionExpected = "Milk, eggs, bread";
		
		ResponseEntity<Task>  responseEntity =
				restTemplate.withBasicAuth(username, password)
							.getForEntity(TASKS_URL + "{taskId}", Task.class, taskId);
		
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		Task task = responseEntity.getBody();
		assertNotNull(task);
		assertAll("Verify task attributes",
			() -> assertThat(task.getTitle()).isEqualTo(titleExpected),
	        () -> assertThat(task.getDescription()).isEqualTo(descriptionExpected)
	        );
	}
	
	@Test
	void shouldReturnAllTaskWhenUserIsAuthenticated() {
		// Arrange
		int totalTasks = 2;
		username = "Alice"; password = "password123";
		String titleExpected = "Book tickets", descriptionExpected = "Vacation tickets to Hawaii";
		
		ResponseEntity<Task[]>  responseEntity =
				restTemplate.withBasicAuth(username, password)
							.getForEntity(TASKS_URL, Task[].class);
		
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		Task[] tasks = responseEntity.getBody();
		assertNotNull(tasks);
		assertThat(tasks.length == totalTasks).withFailMessage("Expected 2 tasks, but found " + tasks.length).isTrue();
		assertThat(tasks[1].getTitle()).isEqualTo(titleExpected);
		assertThat(tasks[1].getDescription()).isEqualTo(descriptionExpected);
	}
	
	@Test
	void shouldCreateTaskWhenAttributesAreValidAndUserIsAuthenticated() {
		// Arrange
		Task task = new Task("New Task", "Testing new tasks creation");
		int newTaskId = 2;
		username = "Charlie"; password = "mypassword";
		
		// Act
		ResponseEntity<Void> responseEntity =
				restTemplate.withBasicAuth(username, password)
							.postForEntity(TASKS_URL, task, Void.class);
		
		// Assert
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(responseEntity.getHeaders().getLocation().getPath()).isEqualTo(TASKS_URL + newTaskId);
	}
	
	@Test
	void shouldUpdateTaskWhenTaskIdAndAttributesAreValid() {
		// Arrange
		Long taskId = 1L;
		username = "Alice"; password = "password123";
		String newTitle = "Buy book", newDescription = "Atomic Habits by James Clear";
		Task task = new Task(newTitle, newDescription);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Task> requestEntity = new HttpEntity<>(task, headers);
		
		// Act
		ResponseEntity<Void> responseEntityHttpMethodPUT =
				restTemplate.withBasicAuth(username, password)
							.exchange(TASKS_URL + "{taskId}", HttpMethod.PUT, requestEntity, Void.class, taskId);
		
		ResponseEntity<Task> responseEntityHttpMethodGET=
				restTemplate.withBasicAuth(username, password)
							.getForEntity(TASKS_URL + "{taskId}", Task.class, taskId);
		
		Task modifiedTask = responseEntityHttpMethodGET.getBody();
		
		// Assert
		assertThat(responseEntityHttpMethodPUT.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertAll("Verify modified task attributes",
				() -> assertThat(modifiedTask.getTitle()).isEqualTo(newTitle),
		        () -> assertThat(modifiedTask.getDescription()).isEqualTo(newDescription)
		        );
	}
	
	@Test
	void shouldDeleteTaskWhenIdIsValid() {
		// Arrange
		Long taskId = 1L;
		int totalTaskExpected = 0;
		username = "Charlie"; password = "mypassword";
		
		// Act
		ResponseEntity<Void> responseEntityHttpMethodDELETE =
				restTemplate.withBasicAuth(username, password)
							.exchange(TASKS_URL + "{taskId}", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, taskId);
		
		ResponseEntity<Task[]>  responseEntityHttpMethodGET =
				restTemplate.withBasicAuth(username, password)
							.getForEntity(TASKS_URL, Task[].class);
		
		
		Task[] tasks = responseEntityHttpMethodGET.getBody();
	
		// Assert
		assertThat(responseEntityHttpMethodDELETE.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(tasks.length == totalTaskExpected);
	}
	
	@Test
	void shouldReturnNotFoundWhenTaskIdIsNotValid() {
		// Arrange
		Long taskId = 6L;
		username = "Alice"; password = "password123";
		String errorMessage = "Invalid task ID.";
		
		// Act
		ResponseEntity<String> responseEntity =
				restTemplate.withBasicAuth(username, password)
							.getForEntity(TASKS_URL + "{taskId}", String.class, taskId);
		
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(responseEntity.getBody()).isEqualTo(errorMessage);
	}
	
	@Test
	void shouldReturnBadRequestWhenTaskAttributesAreNotValid() {
		// Arrange
		username = "Alice"; password = "password123";
		String errorMessage = "Invalid task attributes. Title and description cannot be empty or null.";
		Task task = new Task("", null);
		
		// Act
		ResponseEntity<String> responseEntity =
				restTemplate.withBasicAuth(username, password)
							.postForEntity(TASKS_URL, task, String.class);
		
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(responseEntity.getBody()).isEqualTo(errorMessage);
	}
}
