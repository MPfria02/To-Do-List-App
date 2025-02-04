package todo.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import todo.app.config.SystemTestConfig;
import todo.app.logic.Task;
import todo.app.service.TaskService;

/**
 * Integration test suite for the TaskService implementation, focusing on input validation
 * and error handling scenarios. This test class verifies the robustness of task management
 * operations by testing boundary conditions and invalid inputs.
 * 
 * <p>The test suite uses Spring's testing framework to autowire the TaskService implementation
 * and validates the service's behavior for task creation, retrieval, and deletion operations.
 * Each test case focuses on a specific validation scenario, ensuring that the service properly
 * enforces business rules and data integrity constraints.</p>
 * 
 * @author Marcel Pulido
 * 
 * @see todo.app.service.TaskService
 * @see todo.app.logic.Task
 * @see todo.app.config.SystemTestConfig
 */

@SpringJUnitConfig(SystemTestConfig.class)
class TaskServiceTest {
	
	@Autowired
	TaskService taskService;
	
	/** Task instance used across multiple test cases */
    private Task task;
    
    /** Task and user identifiers used in test cases */
    private Long task_id, user_id;
    
    /** Error message for invalid task attributes */
    private static final String INVALID_TASK_ATTRIBUTES_EXCEPTION_MESSAGE = "Invalid task attributes. Title and description cannot be empty or null.";
    
    /** Error message for invalid task ID */
    private static final String INVALID_TASK_ID_EXCEPTION_MESSAGE = "Invalid task ID.";
    
    /**
     * Verifies that task creation fails when task attributes are empty.
     */
    @Test
    void shouldThrowExceptionWhenTaskAttributesAreEmpty() {
        task = new Task("", "Eggs, milk");
        user_id = 3L;
        
        assertThrows(IllegalArgumentException.class, () -> {
            taskService.saveTask(task, user_id);
        }, INVALID_TASK_ATTRIBUTES_EXCEPTION_MESSAGE);
    }
    
    /**
     * Verifies that task creation fails when task attributes are null.
     */
    @Test
    void shouldThrowExceptionWhenTaskAttributesAreNull() {
        task = new Task("Buy Groceries", null);
        user_id = 1L;
        
        assertThrows(IllegalArgumentException.class, () -> {
            taskService.saveTask(task, user_id);
        }, INVALID_TASK_ATTRIBUTES_EXCEPTION_MESSAGE);
    }
    
    /**
     * Verifies that task retrieval fails with an invalid task ID.
     */
    @Test
    void shouldThrowExceptionWhenTaskIdIsInvalid() {
        task_id = 50L;
        user_id = 1L;
        
        assertThrows(IllegalArgumentException.class, () -> {
            taskService.getTaskById(task_id, user_id);
        }, INVALID_TASK_ID_EXCEPTION_MESSAGE);    
        
        assertThrows(IllegalArgumentException.class, () -> {
            taskService.deleteTaskById(task_id, user_id);
        }, INVALID_TASK_ID_EXCEPTION_MESSAGE); 
    }
}
