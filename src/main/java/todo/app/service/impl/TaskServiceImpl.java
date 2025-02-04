package todo.app.service.impl;

import java.util.List;

import todo.app.logic.Task;
import todo.app.repository.TaskRepository;
import todo.app.service.TaskService;

public class TaskServiceImpl implements TaskService {
	
	private TaskRepository taskRepository;
	
	public TaskServiceImpl(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}
	@Override
	public void saveTask(Task task, Long user_id) {
		// Validates task before insertion
        validateTaskAttributes(task);
        
        taskRepository.createTask(task, user_id);
	}

	@Override
	public Task getTaskById(Long task_id, Long user_id) {
		// Validates task ID before querying
    	validateTaskId(task_id, user_id);
    	
    	return taskRepository.findTaskById(task_id, user_id);
	}

	@Override
	public Long getNextTaskIdForUser(Long user_id) {
		return taskRepository.getNextTaskIdForUser(user_id);
	}
	
	@Override
	public void updateTask(Long task_id, Long user_id, Task task) {
		// Validates task ID before querying
        validateTaskId(task_id, user_id);
    	
    	// Validates task before update
        validateTaskAttributes(task);
        
        taskRepository.updateTask(task_id, user_id, task);

	}

	@Override
	public Task deleteTaskById(Long task_id, Long user_id) {
		// Validates task ID before querying
    	validateTaskId(task_id, user_id);
    	
    	return taskRepository.deleteTaskById(task_id, user_id);
	}

	@Override
	public List<Task> getAllTasks(Long user_id) {
		return taskRepository.getAll(user_id); 
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
        if (!taskRepository.existById(task_id, user_id)) {
            throw new IllegalArgumentException("Invalid task ID.");
        }
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
}
