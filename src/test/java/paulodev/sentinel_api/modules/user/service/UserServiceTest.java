package paulodev.sentinel_api.modules.user.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import paulodev.sentinel_api.exception.custom.user.EmailAlreadyInUseException;
import paulodev.sentinel_api.modules.user.dto.UserRegisterRequest;
import paulodev.sentinel_api.modules.user.dto.UserResponse;
import paulodev.sentinel_api.modules.user.entity.User;
import paulodev.sentinel_api.modules.user.entity.UserRole;
import paulodev.sentinel_api.modules.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Nested
    class GetAllUsers {



    }

    @Nested
    class CreateUser {

        @Test
        void withValidData_ShouldReturnUserResponse() {
            var request = new UserRegisterRequest("Teste", "123456", "teste@teste.com", UserRole.USER);

            when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(request.password())).thenReturn("encrypted-password-test");

            var userCreated = userService.createUser(request);
            assertNotNull(userCreated);

            assertEquals(request.name(), userCreated.name());
            assertEquals(request.email(), userCreated.email());

            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        void withEmailAlreadyRegistered_ShouldReturnErrorResponse() {
            var request = new UserRegisterRequest("Teste", "123456", "teste@teste.com", UserRole.USER);

            when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(new User()));

            assertThrows(EmailAlreadyInUseException.class, () -> userService.createUser(request));

        }

    }

    @Nested
    class GetUserInfo {

        @Test
        void withCorrectData() {
            var user = new User("Paulo", "test@test.com", "123456", UserRole.USER);

            var userInfo = userService.getUserInfo(user);

            assertEquals(user.getName(), userInfo.name());
            assertEquals(user.getEmail(), userInfo.email());
            assertEquals(user.getUserRole(), userInfo.userRole());
        }

    }

}