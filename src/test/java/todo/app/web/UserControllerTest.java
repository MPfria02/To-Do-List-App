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
import todo.app.logic.User;
import todo.app.security.SecurityConfig;
import todo.app.service.UserService;

import static org.mockito.BDDMockito.*;
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
	
	@Test
	@WithMockUser(roles = {"ADMIN"})
	public void shouldReturnUserDetailsWhenRequestedByAdmin() throws Exception {
		// Arrange
		given(userService.getUserById(1L)).willReturn(new User("mockUser", "mock@test.com", "1234"));
		
		// Act & Assert
		mockMvc.perform(get("/todo/app/users/1"))
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
		// Act & Assert
		mockMvc.perform(get("/todo/app/users/1"))
			.andExpect(status().isForbidden());
	}

	@Test
	@WithAnonymousUser
	public void shouldReturnUnauthorizedWhenAnonymousUserTriesToAccessUserById() throws Exception {
		// Act & Assert
		mockMvc.perform(get("/todo/app/users/1"))
			.andExpect(status().isUnauthorized());
	}
	
	@Test
	@WithMockUser(roles = "ADMIN")
	public void shouldReturnAllUsersWhenRequestedByAdmin() throws Exception {
		// Arrange
		List<User> users = Arrays.asList(new User("mockUser", "mock@test.com", "1234"));
		given(userService.getAllUsers()).willReturn(users);
		
		// Act & Assert
		mockMvc.perform(get("/todo/app/users"))
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
		mockMvc.perform(get("/todo/app/users"))
			.andExpect(status().isForbidden());
	}
	
	@Test
	@WithAnonymousUser
	public void shouldReturnUnauthorizedWhenAnonymousUserRequestsUsers() throws Exception {
		// Act & Assert
		mockMvc.perform(get("/todo/app/users"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	public void shouldDeleteUserByIdWhenRequestedByAdmin() throws Exception {
		// Arrange
		given(userService.deleteUserById(1L)).willReturn(new User("mockUser", "mock@test.com", "1234"));
		
		// Act & Assert
		mockMvc.perform(delete("/todo/app/users/1"))
			.andExpect(status().isNoContent());
		
		// Verify
		verify(userService).deleteUserById(1L);
	}
}
