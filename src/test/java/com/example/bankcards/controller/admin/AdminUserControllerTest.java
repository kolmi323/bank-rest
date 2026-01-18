package com.example.bankcards.controller.admin;

import com.example.bankcards.config.SecurityConfiguration;
import com.example.bankcards.config.TestConfig;
import com.example.bankcards.config.TestSecureConfiguration;
import com.example.bankcards.controller.AbstractControllerTest;
import com.example.bankcards.dto.request.user.ChangeRoleRequest;
import com.example.bankcards.dto.request.user.DeleteUserRequest;
import com.example.bankcards.dto.response.user.UserResponse;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.security.JwtRequestFilter;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminUserController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfiguration.class, JwtRequestFilter.class})
)
@Import({TestSecureConfiguration.class, TestConfig.class})
class AdminUserControllerTest extends AbstractControllerTest {
    @MockBean private UserService userService;

    private ChangeRoleRequest changeRoleRequest;
    private DeleteUserRequest deleteUserRequest;
    private UserResponse userResponse;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        changeRoleRequest = new ChangeRoleRequest();
        changeRoleRequest.setUserId(1);
        changeRoleRequest.setUserRole(UserRole.ADMIN);

        deleteUserRequest = new DeleteUserRequest();
        deleteUserRequest.setId(1);

        userResponse = new UserResponse(1, "John Doe", "john@example.com");
    }

    @Test
    public void grantRole_return200_whenValid() throws Exception {
        when(userService.grantRole(USER_ID, changeRoleRequest))
                .thenReturn(userResponse);

        mockMvc.perform(post("/bank/admin/user/grant")
                        .with(authentication(authentication)) // Внедряем админа
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(changeRoleRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(userResponse)));
    }

    @Test
    public void grantRole_return400_whenSameRole() throws Exception {
        doThrow(new BadRequestException("Права уже выданы"))
                .when(userService).grantRole(USER_ID, changeRoleRequest);

        mockMvc.perform(post("/bank/admin/user/grant")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(changeRoleRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void grantRole_return400_whenInvalidBody() throws Exception {
        ChangeRoleRequest invalid = new ChangeRoleRequest();

        mockMvc.perform(post("/bank/admin/user/grant")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void grantRole_return404_whenUserNotFound() throws Exception {
        doThrow(new NotFoundException("Пользователь не найден"))
                .when(userService).grantRole(USER_ID, changeRoleRequest);

        mockMvc.perform(post("/bank/admin/user/grant")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(changeRoleRequest)))
                .andExpect(status().isNotFound());
    }


    @Test
    public void revokeRole_return200_whenValid() throws Exception {
        when(userService.revokeRole(USER_ID, changeRoleRequest))
                .thenReturn(userResponse);

        mockMvc.perform(post("/bank/admin/user/revoke")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(changeRoleRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(userResponse)));
    }

    @Test
    public void revokeRole_return400_whenRevokeNotExistRole() throws Exception {
        doThrow(new BadRequestException("Пользователь не имеет роль админа"))
                .when(userService).revokeRole(USER_ID, changeRoleRequest);

        mockMvc.perform(post("/bank/admin/user/revoke")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(changeRoleRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void revokeRole_return400_whenInvalidBody() throws Exception {
        ChangeRoleRequest invalid = new ChangeRoleRequest();

        mockMvc.perform(post("/bank/admin/user/revoke")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void revokeRole_return404_whenUserNotFound() throws Exception {
        doThrow(new NotFoundException("Пользователь не найден"))
                .when(userService).revokeRole(USER_ID, changeRoleRequest);

        mockMvc.perform(post("/bank/admin/user/revoke")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(changeRoleRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteUser_return200_whenValid() throws Exception {
        mockMvc.perform(delete("/bank/admin/user/delete")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(deleteUserRequest)))
                .andExpect(status().isOk());

        verify(userService).deleteUser(USER_ID, deleteUserRequest);
    }

    @Test
    public void deleteUser_return400_whenTryingToDeleteAdmin() throws Exception {
        doThrow(new BadRequestException("Удаление админа невозможно"))
                .when(userService).deleteUser(USER_ID, deleteUserRequest);

        mockMvc.perform(delete("/bank/admin/user/delete")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(deleteUserRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteUser_return400_whenInvalidBody() throws Exception {
        DeleteUserRequest invalid = new DeleteUserRequest();

        mockMvc.perform(delete("/bank/admin/user/delete")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteUser_return404_whenUserNotFound() throws Exception {
        doThrow(new NotFoundException("Пользователь не найден"))
                .when(userService).deleteUser(USER_ID, deleteUserRequest);
        mockMvc.perform(delete("/bank/admin/user/delete")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(deleteUserRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void viewUser_return200_withDefaultParams() throws Exception {
        Page<UserResponse> pageResponse = new PageImpl<>(List.of(userResponse));

        when(userService.getAllUsers(0, 10)).thenReturn(pageResponse);

        mockMvc.perform(get("/bank/admin/user/view")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(pageResponse)));
    }
}