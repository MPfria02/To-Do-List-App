package todo.app.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import todo.app.config.SystemTestConfig;
import todo.app.exception.UserNotFoundException;
import todo.app.logic.UserDTO;
import todo.app.security.SecurityConfig;
import todo.app.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, SystemTestConfig.class})
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@SuppressWarnings("removal")
	@MockBean
	private UserService userService;
	
	private static final String USERS_URL = "/todo/app/users/";
	
	@Test
	@WithMockUser(roles = {"ADMIN"})
	public void shouldReturnUserDetailsWhenRequestedByAdmin() throws Exception {
		// Arrange
		Long userId = 1L;
		given(userService.getUserById(userId)).willReturn(new UserDTO(1L, "mockUser", "mock@test.com"));
		
		// Act & Assert
		mockMvc.perform(get(USERS_URL + userId))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.username").value("mockUser"))
			.andExpect(jsonPath("$.email").value("mock@test.com"));
		
		// Verify
		verify(userService).getUserById(1L);	
	}
	
	@Test
	@WithMockUser(roles = {"USER"})
	public void shouldReturnForbiddenWhenUnauthorizedUserAccessesEndpoint() throws Exception {
		// Arrange
		Long userId = 1L;
		// Act & Assert
		mockMvc.perform(get(USERS_URL + userId))
			.andExpect(status().isForbidden());
	}
	
	@Test
	@WithMockUser(roles = {"ADMIN"})
	void shouldReturnNotFoundWhenAccessInvalidUserId() throws Exception {
		// Arrange
		Long userId = 5L;
		String exceptionExpectedMessage = "Invalid user ID.";
		given(userService.getUserById(userId)).willThrow(new UserNotFoundException("Invalid user ID."));

		
		// Act & Assert
		mockMvc.perform(get(USERS_URL + userId))
			.andExpect(status().isNotFound())
			.andExpect(result ->  {
			    Throwable ex = result.getResolvedException();
			    assertNotNull(ex);
			    assertTrue(ex instanceof UserNotFoundException);
			    assertEquals(exceptionExpectedMessage, ex.getMessage());
			});
		
		// Verify
		verify(userService).getUserById(userId);
	}

	@Test
	@WithAnonymousUser
	public void shouldReturnUnauthorizedWhenAnonymousUserTriesToAccessUserById() throws Exception {
		// Arrange
		Long userId = 1L;
		// Act & Assert
		mockMvc.perform(get(USERS_URL + userId))
			.andExpect(status().isUnauthorized());
	}
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void shouldReturnAllUsersWhenRequestedByAdmin() throws Exception {
		// Arrange
		List<UserDTO> users = Arrays.asList(new UserDTO(2L, "mockUser", "mock@test.com"));
		given(userService.getAllUsers()).willReturn(users);
		
		// Act & Assert
		mockMvc.perform(get(USERS_URL))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$[0].username").value("mockUser"))
			.andExpect(jsonPath("$[0].email").value("mock@test.com"));
		
		// Verify
		verify(userService).getAllUsers();
	}
	
	@Test
	@WithMockUser(roles = {"USER"})
	public void shouldReturnForbiddenWhenUnauthorizedUserRequestsUsers() throws Exception {
		// Act & Assert
		mockMvc.perform(get(USERS_URL))
			.andExpect(status().isForbidden());
	}
	
	@Test
	@WithAnonymousUser
	public void shouldReturnUnauthorizedWhenAnonymousUserRequestsUsers() throws Exception {
		// Act & Assert
		mockMvc.perform(get(USERS_URL))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	public void shouldDeleteUserByIdWhenRequestedByAdmin() throws Exception {
		// Arrange
		Long userId = 1L;
		given(userService.deleteUserById(userId)).willReturn(new UserDTO(1L, "mockUser", "mock@test.com"));
		
		// Act & Assert
		mockMvc.perform(delete(USERS_URL + userId))
			.andExpect(status().isNoContent());
		
		// Verify
		verify(userService).deleteUserById(1L);
	}
}
