package todo.app.logic;

/**
 * Represents a task in the To-Do List Application.
 * 
 * It represents an individual task that a user needs to complete.
 * 
 * @author Marcel Pulido
 * @version 1.0
 */

public class Task extends Entity {
	
	private String title;
	private String description;
	private Long userId;
	
	/**
     * Parameterized constructor for creating a Task with initial values.
     *
     * @param title       The title or name of the task.
     * @param description A detailed description of the task.
     */
	public Task(String title, String description) {
		super();
		this.title = title;
		this.description = description;
	}

	
	/**
	 * Returns the title of the Task
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the new title for the task
	 * @param title : String
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 
	 * Returns the description of the task
	 */
	public String getDescription() {
		return description;
	}

	/**
	 *  Sets the new description for the task
	 *  @param description : String
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getUserId() {
		return userId;
	}
	
}
