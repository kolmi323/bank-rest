package com.example.bankcards.controller;

import com.example.bankcards.config.SecurityConfiguration;
import com.example.bankcards.config.TestConfig;
import com.example.bankcards.config.TestSecureConfiguration;
import com.example.bankcards.dto.request.user.LoginUserRequest;
import com.example.bankcards.dto.request.user.RegisterUserRequest;
import com.example.bankcards.dto.response.JwtResponse;
import com.example.bankcards.dto.response.user.UserResponse;
import com.example.bankcards.security.JwtRequestFilter;
import com.example.bankcards.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfiguration.class, JwtRequestFilter.class})
)
@Import({TestSecureConfiguration.class, TestConfig.class})
public class AuthControllerTest extends AbstractControllerTest {
    @MockBean
    private AuthService authService;
    private RegisterUserRequest validRegisterRequest;
    private UserResponse userResponse;
    private LoginUserRequest validLoginRequest;
    private JwtResponse jwtResponse;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        validRegisterRequest = new RegisterUserRequest();
        validRegisterRequest.setFirstName("John");
        validRegisterRequest.setLastName("Doe");
        validRegisterRequest.setEmail("test@example.com");
        validRegisterRequest.setPassword("password");

        userResponse = new UserResponse(1, "John Doe", "test@example.com");

        validLoginRequest = new LoginUserRequest();
        validLoginRequest.setEmail("test@example.com");
        validLoginRequest.setPassword("password");

        jwtResponse = new JwtResponse("jwt_token_example");
    }

    @Test
    public void register_return201_whenCalledWithValidArguments() throws Exception {
        when(authService.registerNewUser(validRegisterRequest))
                .thenReturn(userResponse);

        mockMvc.perform(
                        post("/bank/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectWriter.writeValueAsString(validRegisterRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(objectWriter.writeValueAsString(userResponse)));
    }

    @Test
    public void register_return400_whenCalledAlreadyExistsEmail() throws Exception {
        doThrow(constraintViolationSQLAlreadyExistException)
                .when(authService).registerNewUser(validRegisterRequest);
        mockMvc.perform(
                        post("/bank/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectWriter.writeValueAsString(validRegisterRequest))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void register_return400_whenCalledWithNullFields() throws Exception {
        RegisterUserRequest invalidRequest = new RegisterUserRequest();
        invalidRequest.setFirstName("John");

        mockMvc.perform(post("/bank/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void register_return400_whenEmailIsInvalid() throws Exception {
        validRegisterRequest.setEmail("email");

        mockMvc.perform(post("/bank/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void login_return200_whenCalledWithValidCredentials() throws Exception {
        when(authService.loginUser(validLoginRequest))
                .thenReturn(jwtResponse);

        mockMvc.perform(post("/bank/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(jwtResponse)));
    }

    @Test
    public void login_return400_whenCalledWithBadCredentials() throws Exception {
        when(authService.loginUser(any(LoginUserRequest.class)))
                .thenThrow(new BadCredentialsException("Неверная почта или пароль"));

        mockMvc.perform(post("/bank/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(validLoginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void login_return400_whenCalledWithEmptyBody() throws Exception {
        mockMvc.perform(post("/bank/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // Пустой JSON нарушает @NotBlank
                .andExpect(status().isBadRequest());
    }
}