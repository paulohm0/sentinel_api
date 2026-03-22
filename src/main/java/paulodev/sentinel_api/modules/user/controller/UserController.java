package paulodev.sentinel_api.modules.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import paulodev.sentinel_api.modules.user.documentation.UserDocApi;
import paulodev.sentinel_api.modules.user.dto.UserDeactivatedMessage;
import paulodev.sentinel_api.modules.user.dto.UserRegisterRequest;
import paulodev.sentinel_api.modules.user.dto.UserResponse;
import paulodev.sentinel_api.modules.user.dto.UserUpdateInfoRequest;
import paulodev.sentinel_api.modules.user.entity.User;
import paulodev.sentinel_api.modules.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController implements UserDocApi {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody UserRegisterRequest request)
    {
        var response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/info")
    public ResponseEntity<UserResponse> getUserInfo(
            @AuthenticationPrincipal User authenticatedUser)
    {
        var response = userService.getUserInfo(authenticatedUser);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update")
    public ResponseEntity<UserResponse> updateUserInfo(
            @AuthenticationPrincipal User authenticatedUser,
            @Valid @RequestBody UserUpdateInfoRequest update)
    {
        var response = userService.updateUserInfo(authenticatedUser, update);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/delete")
    public ResponseEntity<UserDeactivatedMessage> disableUser(
            @AuthenticationPrincipal User authenticatedUser)
    {
        var response = userService.disableUser(authenticatedUser);
        return ResponseEntity.ok(response);
    }
}
