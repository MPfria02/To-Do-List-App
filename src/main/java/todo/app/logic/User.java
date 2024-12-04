package todo.app.logic;

/**
 * Represents a user in the To-Do List Application.
 * 
 * It holds information related to the user such as personal details, 
 * account credentials.
 */
public class User extends Entity {
	
	private String name;
	private String email;
	private String password;
	
	/**
     * Parameterized constructor for creating a User with initial values.
     *
     * @param name       The name of the user.
     * @param email      The email address of the user
     * @param password   The password for the user account
     */
	public User(String name, String email, String password) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
	}

	/**
	 * 	Returns the password of the user account
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets a new password for the user account
	 * @param password : String
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	

}
