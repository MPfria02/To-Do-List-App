package todo.app.repository;

import java.util.List;

import todo.app.logic.Task;

/**
 * Repository interface for managing CRUD (Create, Read, Update, Delete) operations on
 * tasks in the To-Do List Application.
 * 
 * @author Marcel Pulido
 * @version 1.0
 */
public interface TaskRepository {

    /**
     * Creates a new task in the data source.
     *
     * @param task The task to create.
     * @throws InvalidTaskAttributesException if the task attributes are not valid.
     */
    void createTask(Task task);

    /**
     * Finds a task by its unique identifier.
     *
     * @param task_id The unique identifier of the task to find.
     * @param userId  The identifier of the user who created the task.
     * @return The `Task` object corresponding to the given ID, or null if no task is found.
     * @throws IllegalArgumentException if the given task_id is not valid
     */
    Task findTaskById(Long task_id, Long userId);

    /**
     * Updates an existing task in the data source.
     *
     * @param task The task object with updated values.
     * @throws TaskNotFoundException if no task exists with the given ID.
     * @throws DatabaseException if there's an error during task update
     */
    void updateTask(Long id, Task task);

    /**
     * Deletes a task by its unique identifier.
     *
     * @param task_id The unique identifier of the task to delete.
     * @param userId  The identifier of the user who created the task.
     * @return The deleted `Task` object, or null if no task was found with the given ID.
     */
    Task deleteTaskById(Long task_id, Long userId);
    
    /**
     * Retrieves a list of all tasks in the database.
     * 
     * @return A list of all tasks entities.
     * @throws DatabaseException if there's an error retrieving tasks.
     */
    List<Task> getAll(Long user_id);
    
    /**
     * Validates a task ID against existing tasks for a user.
     * 
     * @param task_id The task ID to validate
     * @param user_id The user ID to check against
     * @return boolean indicating if the task ID is valid
     */
    boolean existById(Long task_id, Long user_id);
    
}
