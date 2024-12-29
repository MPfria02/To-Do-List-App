package todo.app.repository.impl;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import todo.app.logic.Task;
import todo.app.repository.TaskRepository;

/**
 * JDBC implementation of the TaskRepository interface for managing task-related database operations.
 * 
 * This repository provides CRUD operations for tasks using Spring's JdbcTemplate for database interactions.
 * It handles database operations for tasks, including creation, retrieval, update, and deletion.
 * 
 * @author Marcel Pulido
 * @version 1.0
 */
public class JdbcTaskRepository implements TaskRepository {

    /**
     * JdbcTemplate for executing SQL operations.
     * Provides convenient methods for database interactions.
     */
    private JdbcTemplate jdbcTemplate;

    /**
     * Temporary storage for SQL statements used in repository methods.
     */
    private String sqlStatement;

    /**
     * Constructor to initialize JdbcTemplate with a DataSource.
     * 
     * @param dataSource The data source for database connections.
     */
    public JdbcTaskRepository(DataSource dataSource) {
    	
    	if (dataSource == null) throw new IllegalArgumentException("DataSource value is null");
        
    	this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void createTask(Task task) {
        // Validates task before insertion
        validateTaskAttributes(task);

        Long nextTaskId = getNextTaskIdForUser(task.getUserId());
         
        // Inserts task into database
        sqlStatement = "INSERT INTO t_tasks (id, title, description, user_id) values (?,?,?,?)";
        jdbcTemplate.update(sqlStatement,nextTaskId, task.getTitle(), task.getDescription(), task.getUserId());
    }

	@Override
    public Task findTaskById(Long task_id, Long user_id) {
        // Validates task ID before querying
    	validateTaskId(task_id, user_id);

        // Retrieves task from database
        sqlStatement = "SELECT * FROM t_tasks WHERE id = ? and user_id = ?";
        return jdbcTemplate.queryForObject(sqlStatement,
            (rs, rowNumber) -> new Task(rs.getString("title"), 
            							rs.getString("description")),
            task_id, user_id);
    }

    @Override
    public void updateTask(Long task_id, Task task) {
        
    	// Validates task ID before querying
        validateTaskId(task_id, task.getUserId());
    	
    	// Validates task before update
        validateTaskAttributes(task);

        // Updates task in database
        sqlStatement = " UPDATE t_tasks"
            + " SET title = ?, description = ?"
            + " WHERE id = ? ";

        jdbcTemplate.update(sqlStatement, task.getTitle(), 
        								  task.getDescription(), 
        								  task_id);
    }

    @Override
    public Task deleteTaskById(Long task_id, Long user_id) {
        // Retrieves task before deletion
        Task task = findTaskById(task_id, user_id);

        // Deletes task from database
        sqlStatement = "DELETE FROM t_tasks WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sqlStatement, task_id, user_id);

        return task;
    }

    @Override
    public List<Task> getAllTasks(Long user_id) {
        // Retrieves all tasks for a specific user
        sqlStatement = " SELECT * FROM t_tasks"
            + " JOIN t_users"
            + " ON t_tasks.user_id = t_users.id WHERE t_users.id = ?";

        return jdbcTemplate.query(sqlStatement,
            (rs, rowNumber) -> new Task(rs.getString("title"),
            							rs.getString("description")), user_id);
    }
    
    /**
     * Validates the attributes of a task to ensure they meet the required criteria.
     *
     * <p>This method checks if the task has valid attributes, specifically 
     * verifying that the title and description are not null or empty.</p>
     *
     * @param task The task object to be validated
     * @throws IllegalArgumentException if the task attributes are invalid
     */
    private void validateTaskAttributes(Task task) {
        if (!isValidTask(task)) {
            throw new IllegalArgumentException("Invalid task attributes. Title and description cannot be empty or null.");
        }
    }

    /**
     * Validates the task ID in conjunction with the user ID to ensure 
     * the task exists and belongs to the specified user.
     *
     * <p>This method verifies the integrity and ownership of a task 
     * by checking the task ID against the user ID.</p>
     *
     * @param task_id The unique identifier of the task
     * @param user_id The unique identifier of the user
     * @throws IllegalArgumentException if the task ID is invalid
     */
    private void validateTaskId(Long task_id, Long user_id) {
        if (!isValidTaskId(task_id, user_id)) {
            throw new IllegalArgumentException("Invalid task ID.");
        }
    }
    
    /**
     * Validates a task ID against existing tasks for a user.
     * 
     * @param task_id The task ID to validate
     * @param user_id The user ID to check against
     * @return boolean indicating if the task ID is valid
     */
    private boolean isValidTaskId(Long task_id, Long user_id) {
        // Checks if task ID exists and belongs to the user
        sqlStatement = " SELECT COUNT(id)"
            + " FROM t_tasks"
            + " JOIN t_users"
            + " ON t_tasks.user_id = t_users.id WHERE t_users.id = ?";

        int total_IDs = jdbcTemplate.queryForObject(sqlStatement, Integer.class, user_id);

        return (task_id > 0 && task_id <= total_IDs);
    }

    /**
     * Validates a task's basic properties.
     * 
     * @param task The task to validate
     * @return boolean indicating if the task is valid
     */
    private boolean isValidTask(Task task) {
        // Checks if title and description are non-null and non-empty
        boolean isValidTitle = hasValidTitle(task.getTitle());
        boolean isValidDescription = hasValidDescription(task.getDescription());

        return isValidTitle && isValidDescription;
    }
    
    /**
     * Validates the title of a task.
     * 
     * @param title The title to validate.
     * @return {@code true} if the title is valid, 
     * 		   {@code false} otherwise.
     */
    private boolean hasValidTitle(String title) {
    	return (title != null && !title.isEmpty());
    }
    
    /**
     * Validates the description of a task.
     * 
     * @param description The description to validate.
     * @return {@code true} if the description is valid, 
     * 		   {@code false} otherwise.
     */
    private boolean hasValidDescription(String description) {
    	return (description != null  && !description.isEmpty());
    }
    
   private Long getNextTaskIdForUser(Long user_id) {
		
    	sqlStatement = "SELECT COUNT(id) FROM t_tasks WHERE user_id = ?";
		Long max_task_id = jdbcTemplate.queryForObject(sqlStatement, Long.class, user_id); 
		
		return max_task_id + 1;
	}
}
