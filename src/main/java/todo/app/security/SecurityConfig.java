package todo.app.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private DataSource dataSource;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((authz) -> authz
				.requestMatchers(HttpMethod.POST, "/todo/app/register").permitAll())
		.httpBasic(withDefaults())
		.csrf((CsrfConfigurer::disable));
		
		return http.build();
				
	}
	
	@Bean
	public UserDetailsService userDetailsService(DataSource dataSource) {
		   JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
	        
	        // Configure custom queries
	        manager.setUsersByUsernameQuery(
	            "SELECT username, password, enabled FROM t_users WHERE username = ?"
	        );
	        manager.setAuthoritiesByUsernameQuery(
	            "SELECT username, authority FROM t_authorities WHERE username = ?"
	        );
	        
	        return manager;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
}
