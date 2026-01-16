package com.example.bankcards.controller.admin;

import com.example.bankcards.controller.ApiController;
import com.example.bankcards.dto.request.user.ChangeRoleRequest;
import com.example.bankcards.dto.request.user.DeleteUserRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.security.CustomUserDetails;
import com.example.bankcards.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bank/admin/user")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AdminUserController extends ApiController {
    UserService userService;

    @PostMapping("/grant")
    public UserResponse grantRole(
            @RequestBody @Valid ChangeRoleRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return userService.grantRole(userDetails.getId(), request);
    }

    @PostMapping("/revoke")
    public UserResponse revokeRole(
            @RequestBody @Valid ChangeRoleRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return userService.revokeRole(userDetails.getId(), request);
    }

    @PostMapping("/delete")
    public void deleteUser(
            @RequestBody @Valid DeleteUserRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        userService.deleteUser(userDetails.getId(), request);
    }

    @GetMapping("/view")
    public Page<UserResponse> viewUser(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return userService.getAllUsers(page, size);
    }
}
