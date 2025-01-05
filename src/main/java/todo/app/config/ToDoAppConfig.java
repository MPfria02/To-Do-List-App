package todo.app.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import todo.app.repository.TaskRepository;
import todo.app.repository.UserRepository;
import todo.app.repository.impl.JdbcTaskRepository;
import todo.app.repository.impl.JdbcUserRepository;
import todo.app.service.TaskService;
import todo.app.service.UserService;
import todo.app.service.impl.TaskServiceImpl;
import todo.app.service.impl.UserServiceImpl;

@Configuration
public class ToDoAppConfig {

	@Autowired
	private DataSource dataSource;

    @Bean
    public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource);
	}
    
    @Bean
    public TaskRepository taskRepository() {
    	return new JdbcTaskRepository(dataSource);
    }
    
    @Bean
    public UserRepository userRepository() {
    	return new JdbcUserRepository(dataSource);
    }
    
    @Bean
    public UserService userService() {
    	return new UserServiceImpl(userRepository());
    }
    
    @Bean
    public TaskService taskService() {
    	return new TaskServiceImpl(taskRepository());
    }
}
