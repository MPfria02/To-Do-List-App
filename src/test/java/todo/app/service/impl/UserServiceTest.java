package todo.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import todo.app.config.SystemTestConfig;
import todo.app.exception.InvalidUserDataException;
import todo.app.exception.UserNotFoundException;
import todo.app.logic.User;
import todo.app.security.SecurityConfig;
import todo.app.service.UserService;

/**
 * Integration test suite for the UserService implementation, focusing on user management
 * operations and their associated validation rules. This test class ensures the reliability
 * and correctness of user-related operations by testing various edge cases and invalid inputs.
 * 
 * <p>The test suite leverages Spring's testing framework for dependency injection and
 * validates the UserService's behavior across different scenarios. It particularly emphasizes
 * input validation and error handling for user creation, retrieval, and deletion operations.</p>
 * 
 * @author Marcel Pulido
 * 
 * @see todo.app.service.UserService
 * @see todo.app.logic.User
 * @see todo.app.config.SystemTestConfig
 */
@SpringJUnitConfig(classes = {SystemTestConfig.class, SecurityConfig.class})
class UserServiceTest {

	private User user;
	private Long user_id;
	
	@Autowired
	private UserService userService;
    
	// Common test data and SQL queries
    private String username, email, password;
    private String EXCEPTION_MESSAGE_EXPECTED;
    
	/**
     * Verifies that empty user attributes are properly validated during user creation.
     * Tests input validation for empty strings in critical fields.
     */
    @Test
    void shouldThrowExceptionWhenUserAttributesAreEmpty() {
        // Arrange: Create user with empty email and password
        EXCEPTION_MESSAGE_EXPECTED = "User attributes cannot be either null or empty.";
        username = "John Doe"; email = ""; password = "";
        user = new User(username, email, password);
        
        // Act & Assert: Verify exception is thrown for empty attributes
        assertThrows(InvalidUserDataException.class, 
                ()-> { 
                     userService.saveUser(user);
                }, 
                EXCEPTION_MESSAGE_EXPECTED);
    }
    
    /**
     * Verifies that null user attributes are properly validated during user creation.
     * Tests input validation for null values in critical fields.
     */
    @Test
    void shouldThrowExceptionWhenUserAttributesAreNull() {
        // Arrange: Create user with null password
        EXCEPTION_MESSAGE_EXPECTED = "User attributes cannot be either null or empty.";
        username = "John Doe"; email = "john@example.com"; password = null;
        user = new User(username, email, password);
        
        // Act & Assert: Verify exception is thrown for null attributes
        assertThrows(InvalidUserDataException.class, 
                ()-> { 
                     userService.saveUser(user);
                }, 
                EXCEPTION_MESSAGE_EXPECTED);
    }

    /**
     * Verifies that invalid user IDs are properly handled during user retrieval.
     * Tests error handling for non-existent user IDs.
     */
    @Test
    void shouldThrowExceptionWhenUserIdIsNotValid() {
        // Arrange: Set invalid user ID
        user_id = 5L;
        EXCEPTION_MESSAGE_EXPECTED = "Invalid user ID.";
        
        // Act & Assert: Verify exception is thrown for invalid ID
        assertThrows(UserNotFoundException.class, ()->{
               userService.getUserById(user_id);
            }, 
            EXCEPTION_MESSAGE_EXPECTED);
        
        assertThrows(UserNotFoundException.class, ()->{
                userService.deleteUserById(user_id);
            }, 
            EXCEPTION_MESSAGE_EXPECTED);
    }
    
    @Test
    void shouldThrowExceptionWhenUserNameAndPasswordAreNotValid() {
    	username = ""; 
    	EXCEPTION_MESSAGE_EXPECTED = "Invalid username";
    	
    	assertThrows(InvalidUserDataException.class, ()-> {
    		    userService.getUserIdByUsername(username);
    		}, 
    		EXCEPTION_MESSAGE_EXPECTED);
    }
}