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
import paulodev.sentinel_api.modules.user.dto.UserUpdateInfoRequest;
import paulodev.sentinel_api.modules.user.entity.User;
import paulodev.sentinel_api.modules.user.entity.UserRole;
import paulodev.sentinel_api.modules.user.entity.UserStatus;
import paulodev.sentinel_api.modules.user.repository.UserRepository;

import java.util.List;
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

        @Test
        void withUserListNotEmpty_ShouldReturnUserResponseList() {
            var user1 = new User("Paulo", "test@test.com", "1234569", UserRole.USER);
            var user2 = new User("Saulo", "test2@test2.com", "4504564", UserRole.USER);
            var userList = List.of(user1,user2);


            when(userRepository.findAll()).thenReturn(userList);
            var list = userService.getAllUsers();

            assertEquals(list.size(), userList.size());
            assertEquals(list.get(0).email(), userList.get(0).getEmail());
        }

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

    @Nested
    class UpdateUserInfo {

        @Nested
        class WhenDataIsValid {

            @Test
            void onlyUpdateName() {
                var user = new User("Paulo", "test@test.com", "123456", UserRole.USER);
                var updateRequestBody = new UserUpdateInfoRequest("Saulo", null, null);

                var updatedUser = userService.updateUserInfo(user, updateRequestBody);

                assertEquals("Saulo", updatedUser.name());
                assertEquals("test@test.com", updatedUser.email());
                verify(userRepository, times(1)).save(user);
            }

            @Test
            void onlyUpdateEmail() {
                var user = new User("Paulo", "test@test.com", "123456", UserRole.USER);
                var updateRequestBody = new UserUpdateInfoRequest(null, "newemail@test.com", null);

                when(userRepository.findByEmail("newemail@test.com")).thenReturn(Optional.empty());

                var updatedUser = userService.updateUserInfo(user, updateRequestBody);

                assertEquals("Paulo", updatedUser.name());
                assertEquals("newemail@test.com", updatedUser.email());
                assertEquals("Dados Atualizados. Por favor, faça login novamente.", updatedUser.message());
                verify(userRepository, times(1)).save(user);
            }

            @Test
            void onlyUpdatePassword() {
                var user = new User("Paulo", "test@test.com", "123456", UserRole.USER);
                var updateRequestBody = new UserUpdateInfoRequest(null, null, "newpassword");

                when(passwordEncoder.encode("newpassword")).thenReturn("encoded-newpassword");

                var updatedUser = userService.updateUserInfo(user, updateRequestBody);

                assertEquals("Paulo", updatedUser.name());
                assertEquals("test@test.com", updatedUser.email());
                verify(passwordEncoder, times(1)).encode("newpassword");
                verify(userRepository, times(1)).save(user);
            }

            @Test
            void updateAllFields() {
                var user = new User("Paulo", "test@test.com", "123456", UserRole.USER);
                var updateRequestBody = new UserUpdateInfoRequest("Saulo", "newemail@test.com", "newpassword");

                when(userRepository.findByEmail("newemail@test.com")).thenReturn(Optional.empty());
                when(passwordEncoder.encode("newpassword")).thenReturn("encoded-newpassword");

                var updatedUser = userService.updateUserInfo(user, updateRequestBody);

                assertEquals("Saulo", updatedUser.name());
                assertEquals("newemail@test.com", updatedUser.email());
                assertEquals("Dados Atualizados. Por favor, faça login novamente.", updatedUser.message());
                verify(passwordEncoder, times(1)).encode("newpassword");
                verify(userRepository, times(1)).save(user);
            }

        }

        @Nested
        class WhenDataIsInvalid {

            @Test
            void withEmailAlreadyInUse_ShouldThrowException() {
                var user = new User("Paulo", "test@test.com", "123456", UserRole.USER);
                var updateRequestBody = new UserUpdateInfoRequest(null, "existing@test.com", null);

                when(userRepository.findByEmail("existing@test.com")).thenReturn(Optional.of(new User()));

                assertThrows(EmailAlreadyInUseException.class, () -> userService.updateUserInfo(user, updateRequestBody));
            }

            @Test
            void withBlankName_ShouldNotUpdateName() {
                var user = new User("Paulo", "test@test.com", "123456", UserRole.USER);
                var updateRequestBody = new UserUpdateInfoRequest("", null, null);

                var updatedUser = userService.updateUserInfo(user, updateRequestBody);

                assertEquals("Paulo", updatedUser.name());
                verify(userRepository, times(1)).save(user);
            }

            @Test
            void withBlankEmail_ShouldNotUpdateEmail() {
                var user = new User("Paulo", "test@test.com", "123456", UserRole.USER);
                var updateRequestBody = new UserUpdateInfoRequest(null, "", null);

                var updatedUser = userService.updateUserInfo(user, updateRequestBody);

                assertEquals("test@test.com", updatedUser.email());
                verify(userRepository, times(1)).save(user);
            }

            @Test
            void withBlankPassword_ShouldNotUpdatePassword() {
                var user = new User("Paulo", "test@test.com", "123456", UserRole.USER);
                var updateRequestBody = new UserUpdateInfoRequest(null, null, "");

                var updatedUser = userService.updateUserInfo(user, updateRequestBody);

                verify(passwordEncoder, never()).encode(anyString());
                verify(userRepository, times(1)).save(user);
            }

            @Test
            void withSameEmail_ShouldNotUpdateEmail() {
                var user = new User("Paulo", "test@test.com", "123456", UserRole.USER);
                var updateRequestBody = new UserUpdateInfoRequest(null, "test@test.com", null);

                var updatedUser = userService.updateUserInfo(user, updateRequestBody);

                assertEquals("test@test.com", updatedUser.email());
                assertNull(updatedUser.message());
                verify(userRepository, never()).findByEmail(anyString());
                verify(userRepository, times(1)).save(user);
            }

        }

    }

    @Nested
    class DisableUser {

        @Test
        void withActiveUser_ShouldDeactivateAndReturnMessage() {
            var user = new User("Paulo", "test@test.com", "123456", UserRole.USER);
            user.setUserStatus(UserStatus.ACTIVE);

            var result = userService.disableUser(user);

            assertNotNull(result);
            assertEquals(UserStatus.INACTIVE, user.getUserStatus());
            verify(userRepository, times(1)).save(user);
        }

    }


}