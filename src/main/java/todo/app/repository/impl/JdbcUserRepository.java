package todo.app.repository.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
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
		sqlStatement = "INSERT INTO t_users (username, email, password) VALUES (?,?,?)";
		jdbcTemplate.update(sqlStatement, user.getUsername(), user.getEmail(), user.getPassword());		
	}
	
	@Override
	public User findUserById(Long id) {
		sqlStatement = "SELECT * FROM t_users WHERE id = ?";
		return jdbcTemplate.queryForObject(sqlStatement, 
				(rs, rowNum) -> mapToUser(rs, rowNum),
				id);
	}
	
	@Override
	public User findUserByUsername(String username) {
		sqlStatement = "SELECT * FROM t_users WHERE t_users.username = ?";
		return jdbcTemplate.queryForObject(sqlStatement, (rs, rowNum) -> mapToUser(rs, rowNum),
				username);
	}
	
	@Override
	public Long findUserIdByUsername(String name) {
		sqlStatement = "SELECT id FROM t_users WHERE t_users.username = ?";
		return jdbcTemplate.queryForObject(sqlStatement, Long.class, name);
	}
	
	@Override
	public User deleteUserById(Long id) {
		User user = findUserById(id);		
		sqlStatement = "DELETE FROM t_users WHERE id = ?";
		jdbcTemplate.update(sqlStatement, id);
		return user;
	}

	@Override
	public List<User> getAll() {
		sqlStatement = "SELECT * FROM t_users";
		return jdbcTemplate.query(sqlStatement, 
				(rs, rowNum) -> mapToUser(rs, rowNum));
	}
	
	@Override
    public boolean existById(Long id) {
		sqlStatement = "SELECT COUNT(id) FROM t_users WHERE t_users.id = ?";
	    int userIdExists = jdbcTemplate.queryForObject(sqlStatement, Integer.class, id);
	    return userIdExists != 0;
	}
	
    /**
     * Maps a database result set row to a User object.
     * This method is used as a row mapper for database queries.
     * 
     * @param rs the result set containing user data
     * @param rowNumber the current row number
     * @return User object populated with database data
     * @throws SQLException if database access error occurs
     */
    private User mapToUser(ResultSet rs, int rowNumber) throws SQLException {
        return new User(rs.getString("username"), rs.getString("email"), rs.getString("password"));
    }
}
