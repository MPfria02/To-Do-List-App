package todo.app.service.impl;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import todo.app.exception.InvalidUserDataException;
import todo.app.exception.UserNotFoundException;
import todo.app.logic.User;
import todo.app.logic.UserDTO;
import todo.app.mapper.UserMapper;
import todo.app.repository.UserRepository;
import todo.app.service.UserService;

public class UserServiceImpl implements UserService {

	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public void saveUser(User user) {		
		if (!isValidUser(user)) {
			throw new InvalidUserDataException("User attributes cannot be either null or empty.");
		}
		
		User newUser = new User(user.getUsername(), user.getEmail(), passwordEncoder.encode(user.getPassword()));
		userRepository.createUser(newUser);
	}

	@Override
	public UserDTO getUserById(Long id) {
		if (!userRepository.existById(id)) {
			throw new UserNotFoundException("Invalid user ID.");
		}
		User user = userRepository.findUserById(id);
		return UserMapper.toDTO(user);
	}
	
	@Override
	public Long getUserIdByUsername(String username) {
		if (!hasValidUsername(username)) {
			throw new InvalidUserDataException("Invalid username");
		}
		return userRepository.findUserIdByUsername(username);
	}

	@Override
	public UserDTO deleteUserById(Long id) {
		if (!userRepository.existById(id)) {
			throw new UserNotFoundException("Invalid user ID.");
		}
		User user = userRepository.deleteUserById(id);
		return UserMapper.toDTO(user);
	}

	@Override
	public List<UserDTO> getAllUsers() {
		List<User> users = userRepository.getAll();
		List<UserDTO> usersDTO = new LinkedList<>();
		
		for (User user : users) {
			usersDTO.add(UserMapper.toDTO(user));
		}
		return usersDTO;
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
        boolean isValidEmail = hasValidEmail(user.getEmail());
        boolean isValidUserNameAndPassword = validateUserNameAndPassword(
            user.getUsername(), 
            user.getPassword()
        );

        return (isValidEmail && isValidUserNameAndPassword);
    }
    
    /**
     * Validates email and password combination.
     * 
     * Checks both email and password for validity using separate validation methods.
     * 
     * @param email The email to validate
     * @param password The password to validate
     * @return true if both email and password are valid
     */
    private boolean validateUserNameAndPassword(String username, String password) {
        boolean isValidName = hasValidUsername(username);
        boolean isValidPassword = hasValidPassword(password);

        return isValidName && isValidPassword;
    }

    /**
     * Checks if a name is valid (not null and not empty).
     * 
     * @param name The name to validate
     * @return true if the name is valid, false otherwise
     */
    private boolean hasValidUsername(String username) {
        return (username != null && !username.isEmpty());
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
}
