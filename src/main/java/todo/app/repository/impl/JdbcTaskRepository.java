package todo.app.repository.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
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
   
        Long nextTaskId = getNextTaskIdForUser(task.getUserId());
         
        // Inserts task into database
        sqlStatement = "INSERT INTO t_tasks (id, title, description, user_id) values (?,?,?,?)";
        jdbcTemplate.update(sqlStatement,nextTaskId, task.getTitle(), task.getDescription(), task.getUserId());
    }

	@Override
    public Task findTaskById(Long task_id, Long user_id) {
        // Retrieves task from database
        sqlStatement = "SELECT * FROM t_tasks WHERE id = ? and user_id = ?";
        return jdbcTemplate.queryForObject(sqlStatement,
            (rs, rowNumber) -> mapToTask(rs, rowNumber), task_id, user_id);
    }

    @Override
    public void updateTask(Long task_id, Task task) {
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
    public List<Task> getAll(Long user_id) {
        // Retrieves all tasks for a specific user
        sqlStatement = " SELECT * FROM t_tasks"
            + " JOIN t_users"
            + " ON t_tasks.user_id = t_users.id WHERE t_users.id = ?";

        return jdbcTemplate.query(sqlStatement,
            (rs, rowNumber) -> mapToTask(rs, rowNumber), user_id);
    }
    
    @Override
    public boolean existById(Long task_id, Long user_id) {
        // Checks if task ID exists and belongs to the user
        sqlStatement = " SELECT id"
            + " FROM t_tasks"
            + " JOIN t_users"
            + " ON t_tasks.user_id = t_users.id WHERE t_users.id = ?";

        List<Long> userTasks = jdbcTemplate.queryForList(sqlStatement, Long.class, user_id);

        return userTasks.contains(task_id);
    }

   private Long getNextTaskIdForUser(Long user_id) {
		
    	sqlStatement = "SELECT COUNT(id) FROM t_tasks WHERE user_id = ?";
		Long max_task_id = jdbcTemplate.queryForObject(sqlStatement, Long.class, user_id); 
		
		return max_task_id + 1;
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
