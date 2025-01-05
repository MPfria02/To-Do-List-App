package todo.app.service;

import java.util.List;

import todo.app.logic.Task;

/**
 * Provides core functionality for managing tasks within the system. This service handles
 * all CRUD (Create, Read, Update, Delete) operations for tasks, ensuring proper data validation.
 *
 *@author Marcel Pulido
 * @since 1.0
 */
public interface TaskService {

    /**
     * Persists a new task to the storage system. The task must contain all required
     * fields as defined in the Task entity.
     *
     * @param task the task entity to be saved
     * @throws IllegalArgumentException if the task object is null or contains invalid data
     */
    void saveTask(Task task);

    /**
     * Retrieves a specific task by its ID.
     *
     * @param task_id the unique identifier of the task to retrieve
     * @param user_id the ID of the user requesting the task
     * @return the requested Task object if found and accessible to the user
     * @throws IllegalArgumentException if no task exists with the given ID
     */
    Task getTaskById(Long task_id, Long user_id);

    /**
     * Updates an existing task with new information. Only the owner of the task
     * can update the task.
     *
     * @param task_id the unique identifier of the task to update
     * @param task the task object containing the updated information
     * @throws IllegalArgumentException if either parameter is null or if no task exists with the given ID
     */
    void updateTask(Long task_id, Task task);

    /**
     * Removes a task from the system.
     *
     * @param task_id the unique identifier of the task to delete
     * @param user_id the ID of the user requesting the deletion
     * @return the deleted Task object
     * @throws IllegalArgumentException if no task exists with the given ID
     */
    Task deleteTaskById(Long task_id, Long user_id);

    /**
     * Retrieves all tasks associated with a specific user.
     *
     * @param user_id the ID of the user whose tasks should be retrieved
     * @return a List of Task objects associated with the user, empty list if no tasks are found
     */
    List<Task> getAllTasks(Long user_id);
}