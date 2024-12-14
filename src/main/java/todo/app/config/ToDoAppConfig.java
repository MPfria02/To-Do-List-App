package todo.app.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import todo.app.repository.impl.JdbcTaskRepository;
import todo.app.repository.impl.JdbcUserRepository;

@Configuration
public class ToDoAppConfig {

	@Autowired
	private DataSource dataSource;

    @Bean
    public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource);
	}
    
    @Bean
    public JdbcTaskRepository jdbcTaskRepository() {
    	return new JdbcTaskRepository(dataSource);
    }
    
    @Bean
    public JdbcUserRepository jdbcUserRepository() {
    	return new JdbcUserRepository(dataSource);
    }   
}
