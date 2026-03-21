package paulodev.sentinel_api.modules.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import paulodev.sentinel_api.modules.auth.documentation.AuthDocApi;
import paulodev.sentinel_api.modules.auth.dto.LoginResponse;
import paulodev.sentinel_api.modules.auth.service.AuthService;
import paulodev.sentinel_api.modules.user.dto.UserLoginRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController implements AuthDocApi {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        var response = authService.login(request);
        return ResponseEntity.ok(response);
    }

}
