package todo.app.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import todo.app.logic.Task;

/**
 * Test suite for the JdbcTaskRepository class that verifies CRUD operations. Uses an embedded test database for isolation and reproducibility.
 * 
 * @see JdbcTaskRepository
 * @see Task
 */
class JdbcTaskRepositoryTest {

    /** Data source for the embedded test database */
    private DataSource dataSource;
    
    /** Instance of the repository being tested */
    private JdbcTaskRepository jdbcTaskRepository;
    
    /** Template for executing JDBC operations in tests */
    private JdbcTemplate jdbcTemplate;
    
    /** Task instance used across multiple test cases */
    private Task task;
    
    /** Task and user identifiers used in test cases */
    private Long task_id, user_id;
    
    /** SQL query string for dynamic test queries */
    private String SQL_QUERY;
    
    /** SQL query to find a specific task by ID and user ID */
    private static final String FIND_TASK_SQL = "SELECT * FROM t_tasks WHERE t_tasks.id = ? AND t_tasks.user_id = ?";
    
    /** SQL query to count total tasks for a specific user */
    private static final String COUNT_TOTAL_TASKS_FOR_USER_SQL = "SELECT COUNT(id) FROM t_tasks WHERE user_id = ?";
    
    /**
     * Sets up the test environment before each test case.
     * Initializes the test database, repository, and JDBC template.
     * 
     * @throws Exception if database setup fails
     */
    @BeforeEach
    void setUp() throws Exception {
        dataSource = createTestDataSource();
        jdbcTaskRepository = new JdbcTaskRepository(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Verifies that the repository constructor throws an exception when initialized with a null data source.
     */
    @Test
    void shouldThrowExceptionWhenDataSourceIsNull() {
        String exceptionMessage = "DataSource value is null";
        
        assertThrows(IllegalArgumentException.class, () -> {
            jdbcTaskRepository = new JdbcTaskRepository(null);
        }, exceptionMessage);
    }

    /**
     * Tests successful task creation with valid attributes.
     * Verifies the created task's attributes and associated metadata.
     */
    @Test
    void shouldCreateTaskWhenAttributesAreValid() {
        String title = "Buy Groceries at Walmart", description = "Shopping list: bread and eggs";
        user_id = 3L;
        task = new Task(title, description);
        task.setUserId(user_id);
        
        jdbcTaskRepository.createTask(task);
        
        SQL_QUERY = "SELECT * FROM t_tasks WHERE title = ?";
        Task resultTask = jdbcTemplate.queryForObject(SQL_QUERY, 
            (rs, rowNumber) -> mapToTask(rs, rowNumber), title);
        
        assertAll("Verify task attributes",
            () -> assertThat(task.getTitle()).isEqualTo(resultTask.getTitle()),
            () -> assertThat(task.getDescription()).isEqualTo(resultTask.getDescription())
        );

        int totalTasks = jdbcTemplate.queryForObject(COUNT_TOTAL_TASKS_FOR_USER_SQL, Integer.class, user_id),
            totalTasksExpected = 2;
        
        SQL_QUERY = "SELECT id FROM t_tasks WHERE description = ?";
        task_id = jdbcTemplate.queryForObject(SQL_QUERY, Long.class, description);
        Long taskIdExpected = 2L;
        
        assertAll(
            () -> assertThat(totalTasks).isEqualTo(totalTasksExpected),
            () -> assertThat(task_id).isEqualTo(taskIdExpected)
        );
    }
    
    /**
     * Tests successful task retrieval with valid task and user IDs.
     * Verifies the retrieved task's attributes match expected values.
     */
    @Test
    void shouldReturnTaskWhenTaskIdIsValid() {
        task_id = 2L;
        user_id = 1L;
        String titleExpected = "Book tickets";
        String descriptionExpected = "Vacation tickets to Hawaii";
        
        task = jdbcTaskRepository.findTaskById(task_id, user_id);
        
        assertNotNull(task);
        assertAll("Verify task attributes",
            () -> assertThat(task.getTitle()).isEqualTo(titleExpected),
            () -> assertThat(task.getDescription()).isEqualTo(descriptionExpected)
        );
    }
    
    /**
     * Tests successful task update with valid attributes.
     * Verifies the updated task's attributes in the database.
     */
    @Test
    void shouldUpdateTaskWhenTaskAttributesAreValid() {
        task_id = 1L;
        task = new Task("Hello World", "Start programming");
        task.setUserId(1L);
        
        jdbcTaskRepository.updateTask(task_id, task);
        
        Task resultTask = jdbcTemplate.queryForObject(FIND_TASK_SQL, 
            (rs, rowNumber) -> mapToTask(rs, rowNumber), task_id, task.getUserId());
        
        assertAll("Verify task attributes",
            () -> assertThat(task.getTitle()).isEqualTo(resultTask.getTitle()),
            () -> assertThat(task.getDescription()).isEqualTo(resultTask.getDescription())
        );
    }

    /**
     * Tests successful task deletion with a valid task ID.
     * Verifies the deleted task's attributes match the original task.
     */
    @Test
    void shouldDeleteTaskWhenTaskIdIsValid() {
        task_id = 1L;
        user_id = 1L;
        task = jdbcTemplate.queryForObject(FIND_TASK_SQL, 
            (rs, rowNumber) -> mapToTask(rs, rowNumber), task_id, user_id);
        
        Task taskDeleted = jdbcTaskRepository.deleteTaskById(task_id, user_id);
        
        assertNotNull(taskDeleted);
        assertThat(taskDeleted.getTitle()).isEqualTo(task.getTitle());
    }
    
    /**
     * Tests retrieval of all tasks for a specific user.
     * Verifies the total number of tasks matches the expected count.
     */
    @Test
    void shouldGetAllTasks() {
        user_id = 1L;
        int totalTasks = jdbcTemplate.queryForObject(COUNT_TOTAL_TASKS_FOR_USER_SQL, 
            Integer.class, user_id);
        
        List<Task> tasks = jdbcTaskRepository.getAll(user_id);
        
        assertNotNull(tasks);
        assertThat(tasks.size()).isEqualTo(totalTasks);
    }
    
    @Test
    void shouldReturnTrueWhenTaskIdIsValid() {
    	user_id = 1L; task_id = 1L;
    	assertTrue(jdbcTaskRepository.existById(task_id, user_id));
    }
    
    @Test
    void shouldReturnFalseWhenTaskIdNotExist() {
    	user_id = 1L; task_id = 7L;
    	assertFalse(jdbcTaskRepository.existById(task_id, user_id));
    }
    
    
    /**
     * Creates and configures an embedded test database with predefined schema and test data.
     * 
     * @return configured DataSource for the test database
     */
    private DataSource createTestDataSource() {
        return new EmbeddedDatabaseBuilder()
            .setName("To-Do-App")
            .addScript("classpath:todo/testdb/schema.sql")
            .addScript("classpath:todo/testdb/data.sql")
            .build();
    }
    
    /**
     * Maps a database result set row to a Task object.
     * 
     * @param rs the result set containing task data
     * @param rowNumber the current row number
     * @return Task object populated with database data
     * @throws SQLException if database access error occurs
     */
    private Task mapToTask(ResultSet rs, int rowNumber) throws SQLException {
        return new Task(rs.getString("title"), rs.getString("description"));
    }
}
