package todo.app.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import todo.app.logic.User;

/**
 * Test class for {@link JdbcUserRepository} that verifies the functionality of user-related database operations.
 * This class uses an embedded database for testing and follows the AAA (Arrange-Act-Assert) pattern for test structure.
 * The tests cover CRUD operations, validation checks, and error handling for user management.
 *
 * The test suite uses:
 * - JUnit 5 for test execution and assertions
 * - Spring's JdbcTemplate for database operations
 * - An embedded database for isolation and reproducibility
 * 
 * @see JdbcUserRepository
 * @see User
 */
class JdbcUserRepositoryTest {
    
    // Database access and repository objects
    private Long user_id;
    private User user;
    private JdbcUserRepository jdbcUserRepository;
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    
    // Common test data and SQL queries
    private String SQL_QUERY, full_name, email, password;
    private String EXCEPTION_MESSAGE_EXPECTED;
    
    /**
     * Sets up the test environment before each test method.
     * Creates an embedded test database, initializes the JdbcTemplate and repository objects.
     * The database is recreated for each test to ensure test isolation.
     *
     * @throws Exception if database setup fails
     */
    @BeforeEach
    void setUp() throws Exception {
        dataSource = createTestDataSource();
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcUserRepository = new JdbcUserRepository(dataSource);
    }

    /**
     * Verifies that the repository constructor properly validates its DataSource parameter.
     * Tests the defensive programming principle of validating constructor parameters.
     */
    @Test
    void shouldThrowExceptionWhenDataSourceIsNull() {
        // Expected error message for null DataSource
        EXCEPTION_MESSAGE_EXPECTED = "DataSource value is null";
        
        assertThrows(IllegalArgumentException.class, 
                ()-> { 
                    jdbcUserRepository = new JdbcUserRepository(null); 
                }, 
                EXCEPTION_MESSAGE_EXPECTED);
    }

    /**
     * Verifies that empty user attributes are properly validated during user creation.
     * Tests input validation for empty strings in critical fields.
     */
    @Test
    void shouldThrowExceptionWhenUserAttributesAreEmpty() {
        // Arrange: Create user with empty email and password
        EXCEPTION_MESSAGE_EXPECTED = "User attributes cannot be either null or empty.";
        full_name = "John Doe"; email = ""; password = "";
        user = new User(full_name, email, password);
        
        // Act & Assert: Verify exception is thrown for empty attributes
        assertThrows(IllegalArgumentException.class, 
                ()-> { 
                     jdbcUserRepository.createUser(user);
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
        full_name = "John Doe"; email = "john@example.com"; password = null;
        user = new User(full_name, email, password);
        
        // Act & Assert: Verify exception is thrown for null attributes
        assertThrows(IllegalArgumentException.class, 
                ()-> { 
                     jdbcUserRepository.createUser(user);
                }, 
                EXCEPTION_MESSAGE_EXPECTED);
    }

    /**
     * Verifies successful user creation with valid attributes.
     * Tests the happy path for user creation and subsequent retrieval.
     */
    @Test
    void shouldCreateUserWhenAttributesAreValid() {
        // Arrange: Create user with valid attributes
        full_name = "John Doe"; email = "john@example.com"; password = "1234";
        User userExpected = new User(full_name, email, password);
        
        // Act: Create user in database
        jdbcUserRepository.createUser(userExpected);
        
        // Assert: Verify user was created with correct attributes
        SQL_QUERY = "SELECT * FROM t_users WHERE t_users.full_name = ?";
        user = jdbcTemplate.queryForObject(SQL_QUERY, (rs, rowNum) -> mapToUser(rs, rowNum), full_name); 
        
        assertNotNull(user);
        assertAll("Verify user attributes",
                ()-> assertThat(user.getName()).isEqualTo(userExpected.getName()),
                ()-> assertThat(user.getEmail()).isEqualTo(userExpected.getEmail()),
                ()-> assertThat(user.getPassword()).isEqualTo(userExpected.getPassword())
        );    
    }
    
    /**
     * Verifies that empty credentials are properly validated during user authentication.
     * Tests input validation for empty strings in login credentials.
     */
    @Test
    void shouldThrowExceptionWhenUserEmailOrPasswordIsEmpty() {
        // Arrange: Set empty credentials
        EXCEPTION_MESSAGE_EXPECTED = "Email or/and password are incorrect.";
        full_name = "John Doe"; email = ""; password = "";
        
        // Act & Assert: Verify exception is thrown for empty credentials
        assertThrows(IllegalArgumentException.class, 
                ()-> { 
                     jdbcUserRepository.findUserByEmailAndPassword(email, password);
                }, 
                EXCEPTION_MESSAGE_EXPECTED);
    }
    
    /**
     * Verifies that null credentials are properly validated during user authentication.
     * Tests input validation for null values in login credentials.
     */
    @Test
    void shouldThrowExceptionWhenUserEmailOrPasswordIsNull() {
        // Arrange: Set null credentials
        EXCEPTION_MESSAGE_EXPECTED = "Email or/and password are incorrect.";
        full_name = "John Doe"; email = null; password = null;
        
        // Act & Assert: Verify exception is thrown for null credentials
        assertThrows(IllegalArgumentException.class, 
                ()-> { 
                     jdbcUserRepository.findUserByEmailAndPassword(email, password);
                }, 
                EXCEPTION_MESSAGE_EXPECTED);
    }
    
    /**
     * Verifies successful user retrieval using valid email and password.
     * Tests the authentication process using valid credentials.
     */
    @Test
    void shouldReturnUserWhenEmailAndPasswordAreValid() {
        // Arrange: Set valid credentials
        String full_name_expected = "Bob";
        Long user_id_expected = 2L;
        email = "bob@example.com"; password = "securepass";
        
        // Act: Retrieve user with credentials
        SQL_QUERY = "SELECT * FROM t_users WHERE t_users.email = ? AND t_users.password = ?";
        user = jdbcTemplate.queryForObject(SQL_QUERY, (rs, rowNum) -> mapToUser(rs, rowNum), email, password);
        
        // Assert: Verify correct user was retrieved
        assertThat(user.getName()).isEqualTo(full_name_expected);
        
        SQL_QUERY = "SELECT id FROM t_users WHERE t_users.email = ? AND t_users.password = ?";
        user_id = jdbcTemplate.queryForObject(SQL_QUERY, Long.class, email, password);
        
        assertThat(user_id).isEqualTo(user_id_expected);
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
        assertThrows(IllegalArgumentException.class, ()->{
            user = jdbcUserRepository.findUserById(user_id);
            }, 
            EXCEPTION_MESSAGE_EXPECTED);
    }

    /**
     * Verifies successful user retrieval using a valid user ID.
     * Tests the happy path for user retrieval by ID.
     */
    @Test
    void shouldReturnUserWhenUserIdIsValid() {
        // Arrange: Set valid user ID and expected data
        user_id = 1L;
        full_name = "Alice"; email = "alice@example.com"; password = "password123";
        
        // Act: Retrieve user by ID
        user = jdbcUserRepository.findUserById(user_id);
        
        // Assert: Verify correct user was retrieved
        assertNotNull(user);
        assertAll("Verify user attributes",
                ()-> assertThat(user.getName()).isEqualTo(full_name),
                ()-> assertThat(user.getEmail()).isEqualTo(email),
                ()-> assertThat(user.getPassword()).isEqualTo(password)
        );
    }

    /**
     * Verifies successful user deletion and confirms the user no longer exists in the database.
     * Tests both the deletion operation and subsequent verification of deletion.
     */
    @Test
    void shouldDeleteUserWhenIdIsValid() {
        // Arrange: Set valid user ID and expected data
        Long user_id = 1L;
        String nameExpected = "Alice", emailExpected = "alice@example.com", passwordExpected = "password123";
        
        // Act: Delete user
        user = jdbcUserRepository.deleteUserById(user_id);
        
        // Assert: Verify deleted user data was returned correctly
        assertNotNull(user);
        assertAll("Verify user attributes",
                ()-> assertThat(user.getName()).isEqualTo(nameExpected),
                ()-> assertThat(user.getEmail()).isEqualTo(emailExpected),
                ()-> assertThat(user.getPassword()).isEqualTo(passwordExpected)
        );
        
        // Verify user no longer exists in the database
        EXCEPTION_MESSAGE_EXPECTED = "Invalid user ID.";
        
        assertThrows(IllegalArgumentException.class, ()->{
            user = jdbcUserRepository.findUserById(user_id);
            }, 
            EXCEPTION_MESSAGE_EXPECTED);        
    }

    /**
     * Verifies that all users can be retrieved from the database.
     * Tests the retrieval of multiple records and verifies the count matches the database.
     */
    @Test
    void testGetAllUsers() {
        // Get total count of users in database
        SQL_QUERY = "SELECT COUNT(id) FROM t_users";
        int totalUsersInDatabase = jdbcTemplate.queryForObject(SQL_QUERY, Integer.class);
        
        // Retrieve all users and verify count matches
        List<User> users = jdbcUserRepository.getAllUsers();
        
        assertThat(users.size()).isEqualTo(totalUsersInDatabase);
    }
    
    /**
     * Creates and configures an embedded test database with predefined schema and test data.
     * The database is configured using SQL scripts for schema and initial data.
     * 
     * @return configured DataSource for the test database
     */
    private DataSource createTestDataSource() {
        return new EmbeddedDatabaseBuilder()
            .setName("To-Do-App")
            .addScript("classpath:todo/testdb/schema.sql")
            .addScript("classpath:todo/testdb/data.sql")
            .build();
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
        return new User(rs.getString("full_name"), rs.getString("email"), rs.getString("password"));
    }
}
