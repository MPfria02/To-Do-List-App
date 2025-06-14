package todo.app.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import todo.app.ToDoListApplication;
import todo.app.config.SystemTestConfig;
import todo.app.logic.User;
import todo.app.logic.UserDTO;

@SpringBootTest(classes = {ToDoListApplication.class}, 
webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(SystemTestConfig.class)
@TestPropertySource(locations = "classpath:todo/testdb/application-test.properties")
class UserClientTest {

	@Autowired
	TestRestTemplate restTemplate;
	
	private static final String USERS_URL = "/todo/app/users/", REGISTRATION_URL = "/todo/app/register";
	
	private String username, password;
	
	@Test
	void shouldReturnUserWhenRequestedByAdminAndUserIdIsValid() {
		// Arrange
		Long userId = 1L;
		username = "Bob"; password = "securepass";
		UserDTO userDTO;
		String usernameExpected = "Alice", emailExpected = "alice@example.com";
		
		// Act
		ResponseEntity<UserDTO> responseEntity =
				restTemplate.withBasicAuth(username, password)
							.getForEntity(USERS_URL + "{userId}", UserDTO.class, userId);
		
		userDTO = responseEntity.getBody();
		
		// Assert
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertAll("Verify attributes from retrieved user",
				()-> assertThat(userDTO.getUsername()).isEqualTo(usernameExpected),
				()-> assertThat(userDTO.getEmail()).isEqualTo(emailExpected)
		);
	}
	
	@Test
	void shouldReturnAllUsersWhenRequestedByAdmin() {
		// Arrange
		username = "Bob"; password = "securepass";
		int totalUsers = 3;
		UserDTO[] usersDTO;
		
		// Act
		ResponseEntity<UserDTO[]> responseEntity = 
				restTemplate.withBasicAuth(username, password)
							.getForEntity(USERS_URL, UserDTO[].class);
		
		usersDTO = responseEntity.getBody();
		
		// Assert
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(usersDTO.length == totalUsers);
	}
	
	@Test
	void shouldDeleteUserWhenRequestedByAdminAndUserIdIsValid() {
		// Arrange
		int userId;
		int locationHeaderPathLength;
		User newUser = new User("Klassen", "klassen@gmail.com", "passw0rd");
		username = "Bob"; password = "securepass";
		
		// Act
		ResponseEntity<Void> responseEntityHttpMehtodPOST =
				restTemplate.postForEntity(REGISTRATION_URL, newUser, Void.class);
		
		// Dynamically get new user ID
		locationHeaderPathLength = responseEntityHttpMehtodPOST.getHeaders().getLocation().getPath().length();
		userId = Integer.parseInt(responseEntityHttpMehtodPOST.getHeaders().getLocation().getPath().substring(locationHeaderPathLength - 1));
		
		ResponseEntity<Void> responseEntityHttpMethodDELETE = 
				restTemplate.withBasicAuth(username, password)
							.exchange(USERS_URL + "{userId}", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, userId);

		// Assert
		assertThat(responseEntityHttpMehtodPOST.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(responseEntityHttpMethodDELETE.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}
	
	@Test
	void shoulRegisterUserWhenUserDataIsValid() {
		// Arrange
		Long userId = 4L;
		User newUser = new User("Lein", "lein@gmail.com", "passw0rd");
		UserDTO userRetrieved;
		username = "Bob"; password = "securepass";
		
		// Act
		ResponseEntity<Void> responseEntityHttpMehtodPOST =
				restTemplate.postForEntity(REGISTRATION_URL, newUser, Void.class);
		
		ResponseEntity<UserDTO> responseEntityHttpMethodGET =
				restTemplate.withBasicAuth(username, password)
							.getForEntity(USERS_URL + "{userId}", UserDTO.class, userId);
		
		userRetrieved = responseEntityHttpMethodGET.getBody();
		
		// Assert
		assertThat(responseEntityHttpMehtodPOST.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(responseEntityHttpMethodGET.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertAll("Verify new user's attributes",
				()-> assertThat(userRetrieved.getUsername()).isEqualTo(newUser.getUsername()),
				()-> assertThat(userRetrieved.getEmail()).isEqualTo(newUser.getEmail())
		);
	}
	
	@Test
	void shouldReturnNotFoundWhenUserIdIsNotValid() {
		// Arrange
		Long userId = 10L;
		username = "Bob"; password = "securepass";
		String errorMessageExpected = "Invalid user ID.";
		
		// Act
		ResponseEntity<String> responseEntity =
				restTemplate.withBasicAuth(username, password)
							.getForEntity(USERS_URL + "{userId}", String.class, userId);
		
		// Assert
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(responseEntity.getBody()).isEqualTo(errorMessageExpected);
	}
	
	@Test
	void shouldReturnBadRequestWhenUserDataIsNotValid() {
		// Arrange
		User user = new User("", null, null);
		String errorMessageExpected = "User attributes cannot be either null or empty.";
		
		// Act
		ResponseEntity<String> responseEntity = 
				restTemplate.postForEntity(REGISTRATION_URL, user, String.class);
		
		// Assert
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(responseEntity.getBody()).isEqualTo(errorMessageExpected);
	}
}
