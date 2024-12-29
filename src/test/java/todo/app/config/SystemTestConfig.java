package todo.app.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

@Configuration
@Import(ToDoAppConfig.class)
public class SystemTestConfig {
	
	@Bean
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
				.addScript("classpath:todo/testdb/schema.sql")
				.addScript("classpath:todo/testdb/data.sql")
				.build();
	}
}
