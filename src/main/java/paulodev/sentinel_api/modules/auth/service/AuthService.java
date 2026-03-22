package paulodev.sentinel_api.modules.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import paulodev.sentinel_api.config.security.TokenService;
import paulodev.sentinel_api.modules.auth.dto.LoginResponse;
import paulodev.sentinel_api.modules.user.dto.UserLoginRequest;
import paulodev.sentinel_api.modules.user.entity.User;
import paulodev.sentinel_api.modules.user.repository.UserRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager auth;
    private final TokenService tokenService;

    public LoginResponse login(UserLoginRequest request) {
        var credentials = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        var auth = this.auth.authenticate(credentials);
        var token = tokenService.tokenGenerate((User) Objects.requireNonNull(auth.getPrincipal()));
        return new LoginResponse(token);
    }
}
