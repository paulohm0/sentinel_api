package paulodev.sentinel_api.modules.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import paulodev.sentinel_api.config.security.TokenService;
import paulodev.sentinel_api.exception.handler.ErrorResponse;
import paulodev.sentinel_api.modules.auth.dto.LoginResponse;
import paulodev.sentinel_api.modules.user.dto.UserLoginRequest;
import org.springframework.security.core.Authentication;
import paulodev.sentinel_api.modules.user.entity.User;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager auth;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_WithValidCredentials_ShouldReturnToken() {
        var request = new UserLoginRequest("paulo@test.com", "123456");
        var response = new LoginResponse("access-token-test");

        var authTest = mock(Authentication.class);
        when(auth.authenticate(any())).thenReturn(authTest);

        var userTest = new User();
        when(authTest.getPrincipal()).thenReturn(userTest);
        when(tokenService.tokenGenerate(any())).thenReturn("access-token-test");

        var result = authService.login(request);

        assertEquals(response.accessToken(), result.accessToken());
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnErrorResponse() {
        var request = new UserLoginRequest("", "");
        when(auth.authenticate(any())).thenThrow( new BadCredentialsException("Credenciais Invalidas"));
        assertThrows(BadCredentialsException.class, () -> { authService.login(request); });
    }
}