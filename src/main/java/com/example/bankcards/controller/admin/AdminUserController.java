package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.request.user.ChangeRoleRequest;
import com.example.bankcards.dto.request.user.DeleteUserRequest;
import com.example.bankcards.dto.response.ErrorResponse;
import com.example.bankcards.dto.response.user.UserResponse;
import com.example.bankcards.security.CustomUserDetails;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Admin User API",
        description = "Административное управление пользователями: выдача/отзыв ролей, удаление и просмотр"
)
@RestController
@RequestMapping("/bank/admin/user")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AdminUserController {
    UserService userService;

    @Operation(summary = "Выдача прав (роли) пользователю")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Права успешно выданы",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка: права уже выданы или попытка изменить свои права",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/grant")
    public UserResponse grantRole(
            @RequestBody @Valid ChangeRoleRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return userService.grantRole(userDetails.getId(), request);
    }

    @Operation(summary = "Отзыв прав (роли) у пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Права успешно отняты",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка: попытка забрать несуществующие права или изменить свои права",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/revoke")
    public UserResponse revokeRole(
            @RequestBody @Valid ChangeRoleRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return userService.revokeRole(userDetails.getId(), request);
    }

    @Operation(summary = "Удаление пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно удален"),
            @ApiResponse(responseCode = "400", description = "Ошибка: нельзя удалить пользователя с ролью администратора или самого себя",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/delete")
    public void deleteUser(
            @RequestBody @Valid DeleteUserRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        userService.deleteUser(userDetails.getId(), request);
    }

    @Operation(summary = "Просмотр списка всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей получен успешно")
    })
    @GetMapping("/view")
    public Page<UserResponse> viewUser(
            @Parameter(description = "Номер страницы")
            @Min(0) @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "Количество элементов")
            @Min(1) @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return userService.getAllUsers(page, size);
    }
}
