package paulodev.sentinel_api.modules.user.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import paulodev.sentinel_api.config.security.TokenService;
import paulodev.sentinel_api.modules.user.dto.UserDeactivatedMessage;
import paulodev.sentinel_api.modules.user.dto.UserRegisterRequest;
import paulodev.sentinel_api.modules.user.dto.UserResponse;
import paulodev.sentinel_api.modules.user.dto.UserUpdateInfoRequest;
import paulodev.sentinel_api.modules.user.entity.User;
import paulodev.sentinel_api.modules.user.entity.UserRole;
import paulodev.sentinel_api.exception.custom.user.EmailAlreadyInUseException;
import paulodev.sentinel_api.modules.user.repository.UserRepository;
import paulodev.sentinel_api.modules.user.service.UserService;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class,
            excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class}
            )
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    @Nested
    class ListAllUsers {

        @Test
        void withCorrectData() throws Exception {
            User user1 = new User("Paulo", "paulo@test.com", "123", UserRole.USER);
            User user2 = new User("Saulo", "saulo@test.com", "456", UserRole.USER);

            var userResponse1 = new UserResponse(user1);
            var userResponse2 = new UserResponse(user2);
            var response = List.of(userResponse1, userResponse2);

            when(userService.getAllUsers()).thenReturn(response);

            mockMvc.perform(get("/user/list")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()", is(response.size())))
                    .andExpect(jsonPath("$[0].name", is(response.get(0).name())));
        }

        @Test
        void withEmptyList() throws Exception {
            when(userService.getAllUsers()).thenReturn(List.of());

            mockMvc.perform(get("/user/list")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()", is(0)));
        }

        @Test
        void withSingleUser() throws Exception {
            User user = new User("Name", "email@test.com", "pass", UserRole.USER);
            UserResponse response = new UserResponse(user);

            when(userService.getAllUsers()).thenReturn(List.of(response));

            mockMvc.perform(get("/user/list")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()", is(1)))
                    .andExpect(jsonPath("$[0].name", is("Name")));
        }

    }

    @Nested
    class Register {

        @Test
        void withValidRequest() throws Exception {
            UserRegisterRequest request = new UserRegisterRequest("Name", "pass123", "email@test.com", UserRole.USER);
            User user = new User("Name", "email@test.com", "pass", UserRole.USER);
            UserResponse response = new UserResponse(user);

            when(userService.createUser(request)).thenReturn(response);

            mockMvc.perform(post("/user/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name", is("Name")));
        }

        @Test
        void withBlankName() throws Exception {
            UserRegisterRequest request = new UserRegisterRequest("", "pass123", "email@test.com", UserRole.USER);

            mockMvc.perform(post("/user/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void withInvalidEmail() throws Exception {
            UserRegisterRequest request = new UserRegisterRequest("Name", "pass123", "invalid", UserRole.USER);

            mockMvc.perform(post("/user/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void withShortPassword() throws Exception {
            UserRegisterRequest request = new UserRegisterRequest("Name", "123", "email@test.com", UserRole.USER);

            mockMvc.perform(post("/user/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void withNullRole() throws Exception {
            String json = "{\"name\":\"Name\",\"password\":\"pass123\",\"email\":\"email@test.com\"}";

            mockMvc.perform(post("/user/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void withEmailAlreadyInUse() throws Exception {
            UserRegisterRequest request = new UserRegisterRequest("Name", "pass123", "email@test.com", UserRole.USER);

            when(userService.createUser(request)).thenThrow(new EmailAlreadyInUseException());

            mockMvc.perform(post("/user/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }

    }

    @Nested
    class GetUserInfo {

        @Test
        void withAuthenticatedUser() throws Exception {
            User user = new User("Name", "email@test.com", "pass", UserRole.USER);
            UserResponse response = new UserResponse(user);

            when(userService.getUserInfo(user)).thenReturn(response);

            mockMvc.perform(get("/user/info")
                    .with(SecurityMockMvcRequestPostProcessors.user(user)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("Name")));
        }

    }

    @Nested
    class UpdateUserInfo {

        @Test
        void withValidUpdate() throws Exception {
            User authenticatedUser = new User("OldName", "old@test.com", "pass", UserRole.USER);
            UserUpdateInfoRequest update = new UserUpdateInfoRequest("NewName", "new@test.com", "newpass123");
            User updatedUser = new User("NewName", "new@test.com", "newpass", UserRole.USER);
            UserResponse response = new UserResponse(updatedUser);

            when(userService.updateUserInfo(authenticatedUser, update)).thenReturn(response);

            mockMvc.perform(patch("/user/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(update))
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("NewName")));
        }

        @Test
        void withInvalidEmail() throws Exception {
            User authenticatedUser = new User("Name", "email@test.com", "pass", UserRole.USER);
            UserUpdateInfoRequest update = new UserUpdateInfoRequest("NewName", "invalid", "newpass123");

            mockMvc.perform(patch("/user/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(update))
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void withShortPassword() throws Exception {
            User authenticatedUser = new User("Name", "email@test.com", "pass", UserRole.USER);
            UserUpdateInfoRequest update = new UserUpdateInfoRequest("NewName", "new@test.com", "123");

            mockMvc.perform(patch("/user/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(update))
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isBadRequest());
        }

    }

    @Nested
    class DisableUser {

        @Test
        void withSuccess() throws Exception {
            User authenticatedUser = new User("Name", "email@test.com", "pass", UserRole.USER);
            UserDeactivatedMessage response = new UserDeactivatedMessage();

            when(userService.disableUser(authenticatedUser)).thenReturn(response);

            mockMvc.perform(delete("/user/delete")
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", is("Usuário desativado com sucesso")));
        }

    }

}