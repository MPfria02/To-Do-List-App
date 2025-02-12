package todo.app.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import todo.app.config.SystemTestConfig;
import todo.app.exception.InvalidTaskDataException;
import todo.app.exception.InvalidUserDataException;
import todo.app.logic.Task;
import todo.app.logic.User;
import todo.app.security.SecurityConfig;
import todo.app.service.UserService;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, SystemTestConfig.class})
class AuthControllerTest {

	@Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
	@MockBean
    private UserService userService;

    @Test
    void shouldRegisterUserWhenCredentialsAreValid() throws Exception {
        // Arrange
        String username = "MockUser";
        Long userId = 4L;
        
        // Mock the userService methods
        willDoNothing().given(userService).saveUser(any(User.class));
        given(userService.getUserIdByUsername(username)).willReturn(userId);

        // Create the JSON request body
        String requestBody = createMockUserJson();

        // Act & Assert
        mockMvc.perform(post("/todo/app/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "http://localhost/todo/app/users/4"));

        // Verify that saveUser was called
        verify(userService).saveUser(any(User.class));
        verify(userService).getUserIdByUsername(username);
    }
    
	@Test
	void shouldReturnBadRequestWhenUserDataIsNotValid() throws Exception {
		// Arrange
		String exceptionExpectedMessage = "User attributes cannot be either null or empty.";
		willThrow(new InvalidUserDataException(exceptionExpectedMessage)).given(userService).saveUser(any(User.class));
		
		// Act & Assert
		mockMvc.perform(post("/todo/app/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(createInvalidMockUserJson()))
			.andExpect(status().isBadRequest())
			.andExpect(result -> {
			    Throwable ex = result.getResolvedException();
			    assertNotNull(ex);
			    assertTrue(ex instanceof InvalidUserDataException);
			    assertEquals(exceptionExpectedMessage, ex.getMessage());
			});
		
		// Verify
		verify(userService).saveUser(any(User.class));
	}

	private String createInvalidMockUserJson() {
		String mockUserJson = """
	            {
	                "username": "MockUser",
	                "email": "mock@test.com",
	                "password": ""
	            }
	            """;
			
			return mockUserJson;
	}

	private String createMockUserJson() {
		String mockUserJson = """
            {
                "username": "MockUser",
                "email": "mock@test.com",
                "password": "1234"
            }
            """;
		
		return mockUserJson;
	}
}
