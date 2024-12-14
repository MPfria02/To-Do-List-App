package todo.app.repository;

import java.util.List;

import todo.app.logic.User;

/**
 * Repository interface for managing user-related database operations.
 * Provides CRUD (Create, Read, Update, Delete) operations for User entities.
 * 
 * @author Marcel Pulido 
 * @version 1.0
 */
public interface UserRepository {
    /**
     * Creates a new user in the database.
     * 
     * @param user The user entity to be created
     * @throws IllegalArgumentException if the user is null or invalid
     * @throws DatabaseException if there's an error during user creation
     */
    void createUser(User user);

    /**
     * Finds a user by their email and password credentials.
     * 
     * @param email The user's email address
     * @param password The user's password
     * @return The User object if credentials are valid
     * @throws InvalidCredentialsException if email or password are incorrect
     * @throws UserNotFoundException if no user matches the credentials
     */
    User findUserByEmailAndPassword(String email, String password);
    
    /**
     * Finds a user by their email and password credentials.
     * 
     * @param id The unique identifier of the user.
     * @return The User object if credentials are valid.
     * @throws InvalidCredentialsException if id is incorrect.
     * @throws UserNotFoundException if no user matches the credentials.
     */
    User findUserById(Long id);
    

    /**
     * Deletes a user from the database by their unique identifier.
     * 
     * @param id The unique identifier of the user to be deleted
     * @return The deleted User object
     * @throws UserNotFoundException if no user exists with the given ID
     * @throws DatabaseException if there's an error during user deletion
     */
    User deleteUserById(Long id);

    /**
     * Retrieves a list of all users in the database.
     * 
     * @return A list of all User entities
     * @throws DatabaseException if there's an error retrieving users
     */
    List<User> getAllUsers();
}
