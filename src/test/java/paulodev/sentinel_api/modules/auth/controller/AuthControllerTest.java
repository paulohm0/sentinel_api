package paulodev.sentinel_api.modules.auth.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import paulodev.sentinel_api.config.security.TokenService;
import paulodev.sentinel_api.modules.auth.dto.LoginResponse;
import paulodev.sentinel_api.modules.auth.service.AuthService;
import paulodev.sentinel_api.modules.user.dto.UserLoginRequest;
import paulodev.sentinel_api.modules.user.repository.UserRepository;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AuthController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc; // simula client request

    @Autowired
    private ObjectMapper objectMapper; // toJson

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private TokenService tokenService;

    @Nested
    class Login {

        @Test
        void withValidCredentials_ShouldReturnOk() throws Exception {
            var request = new UserLoginRequest("paulo@test.com", "123456");
            var response = new LoginResponse("access-token-test");

            when(authService.login(any())).thenReturn(response);

            mockMvc.perform(post("/auth/login")                                         // Define a rota e o métod0 HTTP
                            .contentType(MediaType.APPLICATION_JSON)                    // Informa que esta sendo enviando um JSON
                            .content(objectMapper.writeValueAsString(request)))         // Transforma o objeto 'request' em texto JSON
                    .andExpect(status().isOk())                                         // Verifica se o servidor respondeu o status code correto
                    .andExpect(result -> {                                              // Verifica se o corpo da resposta contém o token que o dublê entregou
                        String json = result.getResponse().getContentAsString();
                        assertTrue(json.contains("access-token-test"));
                    });
        }

        @Test
        void withInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
            var request = new UserLoginRequest("test", "123456");

            when(authService.login(any())).thenThrow( new BadCredentialsException("erro"));

            mockMvc.perform(post("/auth/login")                                         // Define a rota e o métod0 HTTP
                            .contentType(MediaType.APPLICATION_JSON)                    // Informa que esta sendo enviando um JSON
                            .content(objectMapper.writeValueAsString(request)))         // Transforma o objeto 'request' em texto JSON
                    .andExpect(status().isUnauthorized())                               // Verifica se o servidor respondeu o status code correto
                    .andExpect(result -> {                                              // Verifica se o corpo da resposta contém o token que o dublê entregou
                        String json = result.getResponse().getContentAsString();
                        assertTrue(json.contains("erro"));
                    });
        }

        @Test
        void withEmptyCredentials_ShouldReturnBadRequest() throws Exception {
            var request = new UserLoginRequest("", "");

            mockMvc.perform(post("/auth/login")                                         // Define a rota e o métod0 HTTP
                            .contentType(MediaType.APPLICATION_JSON)                    // Informa que esta sendo enviando um JSON
                            .content(objectMapper.writeValueAsString(request)))         // Transforma o objeto 'request' em texto JSON
                    .andExpect(status().isBadRequest());                                 // Verifica se o servidor respondeu o status code correto
        }
    }
}