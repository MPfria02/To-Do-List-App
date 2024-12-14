package todo.app.repository.impl;

import java.util.List;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import todo.app.logic.User;
import todo.app.repository.UserRepository;

/**
 * Repository implementation for managing user data using JDBC (Java Database Connectivity).
 * 
 * This class provides database operations for user-related functionality, including
 * creating, finding, deleting, and retrieving users from a database using Spring's JdbcTemplate.
 * 
 * The implementation performs validation on user data before executing database operations
 * to ensure data integrity and prevent invalid insertions or queries.
 * 
 * @author Marcel Pulido
 * @version 1.0
 * @see UserRepository
 * @see JdbcTemplate
 * @see DataSource
 */
public class JdbcUserRepository implements UserRepository {

    /** 
     * JDBC template for executing SQL operations with simplified database interaction.
     * Provides methods for common database operations like query, update, etc.
     */
    private JdbcTemplate jdbcTemplate;

    /** 
     * Temporary storage for SQL statements to be executed.
     * Note: In a production environment, consider using prepared statements 
     * or a more robust SQL management approach.
     */
    private String sqlStatement;

    /**
     * Constructs a new JdbcUserRepository with the specified data source.
     * 
     * @param dataSource The data source providing database connections
     * @throws IllegalArgumentException if the provided data source is null
     */
    public JdbcUserRepository(DataSource dataSource) {
    	
    	if (dataSource == null) throw new IllegalArgumentException("DataSource value is null");
    	
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
	
	@Override
	public void createUser(User user) {
		
		if (!isValidUser(user)) {
			throw new IllegalArgumentException("User attributes cannot be either null or empty.");
		}
		
		sqlStatement = "INSERT INTO t_users (full_name, email, password) VALUES (?,?,?)";
		jdbcTemplate.update(sqlStatement, user.getName(), user.getEmail(), user.getPassword());		
	}
	
	@Override
	public User findUserByEmailAndPassword(String email, String password) {
		
		validateUserEmailAndPassword(email, password);
		sqlStatement = "SELECT * FROM t_users WHERE email = ? AND password = ?";
		return jdbcTemplate.queryForObject(sqlStatement, 
				(rs, rowNum) -> new User(rs.getString("full_name"), 
							             rs.getString("email"), 
							             rs.getString("password")),
				email, password);
	}

	@Override
	public User findUserById(Long id) {

		validateUserId(id);
		sqlStatement = "SELECT * FROM t_users WHERE id = ?";
		return jdbcTemplate.queryForObject(sqlStatement, 
				(rs, rowNum) -> new User(rs.getString("full_name"), 
        				 				 rs.getString("email"), 
        				 				 rs.getString("password")),
				id);
	}
	
	@Override
	public User deleteUserById(Long id) {
		validateUserId(id);
		User user = findUserById(id);		
		sqlStatement = "DELETE FROM t_users WHERE id = ?";
		jdbcTemplate.update(sqlStatement, id);
		return user;
	}

	@Override
	public List<User> getAllUsers() {
		
		sqlStatement = "SELECT * FROM t_users";
		return jdbcTemplate.query(sqlStatement, 
				(rs, rowNum) -> new User(rs.getString("full_name"), 
        				 				 rs.getString("email"), 
        				 				 rs.getString("password")));
	}
	
    /**
     * Validates email and password combination.
     * 
     * Checks both email and password for validity using separate validation methods.
     * 
     * @param email The email to validate
     * @param password The password to validate
     * @return true if both email and password are valid
     * @throws IllegalArgumentException if either email or password is invalid
     */
    private boolean validateUserEmailAndPassword(String email, String password) {
        boolean isValidEmail = hasValidEmail(email);
        boolean isValidPassword = hasValidPassword(password);

        if (!isValidEmail || !isValidPassword) {
            throw new IllegalArgumentException("Email or/and password are incorrect.");
        }
        return true;
    }

    /**
     * Validates a user ID to ensure it is within the valid range.
     * 
     * Checks if the ID is positive and does not exceed the total number of users.
     * 
     * @param id The user ID to validate
     * @throws IllegalArgumentException if the ID is invalid
     */
    private void validateUserId(Long id) {
        if (!isValidUserId(id)) {
            throw new IllegalArgumentException("Invalid user ID.");
        }
    }

    /**
     * Performs comprehensive validation of a user object.
     * 
     * Checks:
     * - User name is valid
     * - Email is valid
     * - Password is valid
     * 
     * @param user The user object to validate
     * @return {@code true} if all user attributes are valid, 
     * 		   {@code false} otherwise
     */
    private boolean isValidUser(User user) {
        boolean isValidName = hasValidName(user.getName());
        boolean isValidUserEmailAndPassword = validateUserEmailAndPassword(
            user.getEmail(), 
            user.getPassword()
        );

        return (isValidName && isValidUserEmailAndPassword);
    }

    /**
     * Checks if a name is valid (not null and not empty).
     * 
     * @param name The name to validate
     * @return true if the name is valid, false otherwise
     */
    private boolean hasValidName(String name) {
        return (name != null && !name.isEmpty());
    }

    /**
     * Checks if a password is valid (not null and not empty).
     * 
     * @param password The password to validate
     * @return true if the password is valid, false otherwise
     */
    private boolean hasValidPassword(String password) {
        return (password != null && !password.isEmpty());
    }

    /**
     * Checks if an email is valid (not null and not empty).
     * 
     * @param email The email to validate
     * @return {@code true} if the email is valid, {@code false} otherwise
     */
    private boolean hasValidEmail(String email) {
        return (email != null && !email.isEmpty());
    }

    /**
     * Validates a user ID against the total number of users in the database.
     * 
     * Checks if the ID is:
     * - Positive
     * - Not greater than the total number of users
     * 
     * @param id The user ID to validate
     * @return true if the ID is valid, false otherwise
     */
    private boolean isValidUserId(Long id) {
        sqlStatement = "SELECT COUNT(id) FROM t_users";
        int totalUsers = jdbcTemplate.queryForObject(sqlStatement, Integer.class);

        return id > 0 && id <= totalUsers;
    }
}
