package todo.app.logic;

/**
 * Represents a user in the To-Do List Application.
 * 
 * It holds information related to the user such as personal details, 
 * account credentials.
 * 
 * @author Marcel Pulido
 * @version 1.0
 */
public class User extends Entity {
	
	private String username;
	private String email;
	private String password;
	
	/**
     * Parameterized constructor for creating a User with initial values.
     *
     * @param name       The name of the user.
     * @param email      The email address of the user
     * @param password   The password for the user account
     */
	public User(String username, String email, String password) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
	}
	
	/**
	 * 	Returns the name of the user account
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * 	Returns the email of the user account
	 */
	public String getEmail() {
		return email;
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
