package todo.app;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import todo.app.logic.User;
import todo.app.repository.UserRepository;

@SpringBootApplication
public class ToDoListApplication {

	public static void main(String[] args) {
		SpringApplication.run(ToDoListApplication.class, args);
	}
	
    @Bean
    CommandLineRunner encodeExistingPasswords(UserRepository userRepository) {
        return args -> {
            // Create the encoder
            PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
            
            // Get all users
            List<User> users = userRepository.getAll();
            
            // Update each user's password
            users.forEach(user -> {
                // Only encode if the password isn't already encoded
                if (!user.getPassword().startsWith("$2a$")) {
                    String encodedPassword = encoder.encode(user.getPassword());
                    user.setPassword(encodedPassword);
                    userRepository.updateUser(user);
                }
            });
        };
    }

}
