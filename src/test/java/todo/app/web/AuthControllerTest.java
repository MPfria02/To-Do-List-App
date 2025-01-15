package todo.app.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import todo.app.config.SystemTestConfig;
import todo.app.logic.User;
import todo.app.security.SecurityConfig;
import todo.app.service.UserService;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, SystemTestConfig.class})
class AuthControllerTest {

//    @Autowired
//    private WebApplicationContext context;

	@Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

//    @BeforeEach
//    void setup() {
//        // Configure MockMvc with security
//        this.mockMvc = MockMvcBuilders
//            .webAppContextSetup(context)
//            .build();
//    }

    @Test
    void handleUserRegistrationRequest() throws Exception {
        // Arrange
        String username = "MockUser";
        User user = new User(username, "mock@test.com", "1234");
        
        // Mock the userService methods
        doNothing().when(userService).saveUser(any(User.class));
        when(userService.getUserIdByUsername(username)).thenReturn(4L);

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
