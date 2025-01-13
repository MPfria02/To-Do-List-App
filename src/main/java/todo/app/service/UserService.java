package todo.app.service;

import java.util.List;

import todo.app.logic.User;

/**
 * Provides core operations for managing user entities in the system.
 * Implements standard CRUD operations for user management with additional
 * data validation.
 *
 * @since 1.0
 */
public interface UserService {
    
    /**
     * Persists a new user in the system.
     *
     * @param user the user entity to be saved, must not be null
     * @throws IllegalArgumentException if either user is null or its parameters are null or empty
     */
    void saveUser(User user);
    
    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the unique identifier of the user, must not be null
     * @return the found user entity
     * @throws IllegalArgumentException if no user exists with given ID
     */
    User getUserById(Long id);
    
    /**
     * Finds a user id by their username
     * 
     * @param username The username of the user.
     * @return The User ID if username is valid
     * @throws IllegalArgumentException if not user is found with the given username
     */
    Long getUserIdByUsername(String username);
    
    /**
     * Removes a user from the system by their ID.     *
     * @param id the unique identifier of the user to delete, must not be null
     * @return the deleted user entity
     * @throws IllegalArgumentException if no user found with given ID
     */
    User deleteUserById(Long id);
    
    /**
     * Retrieves all users currently in the system.
     *
     * @return a List containing all users, empty list if no users exists
     */
    List<User> getAllUsers();
}
