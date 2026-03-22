package paulodev.sentinel_api.modules.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import paulodev.sentinel_api.exception.custom.user.EmailAlreadyInUseException;
import paulodev.sentinel_api.modules.user.dto.UserDeactivatedMessage;
import paulodev.sentinel_api.modules.user.dto.UserRegisterRequest;
import paulodev.sentinel_api.modules.user.dto.UserResponse;
import paulodev.sentinel_api.modules.user.dto.UserUpdateInfoRequest;
import paulodev.sentinel_api.modules.user.entity.User;
import paulodev.sentinel_api.modules.user.entity.UserRole;
import paulodev.sentinel_api.modules.user.entity.UserStatus;
import paulodev.sentinel_api.modules.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(UserRegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyInUseException();
        }
        String encryptedPassword = passwordEncoder.encode(request.password());
        User newUser = new User(
                null,
                request.name(),
                request.email(),
                encryptedPassword,
                UserRole.USER,
                UserStatus.ACTIVE,
                new ArrayList<>());
        userRepository.save(newUser);
        return new UserResponse(
                newUser.getId(),
                newUser.getName(),
                newUser.getEmail(),
                newUser.getUserRole());
    }

    public UserResponse getUserInfo(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getUserRole());
    }

    @Transactional
    public UserResponse updateUserInfo(User user, UserUpdateInfoRequest update) {

        Optional.ofNullable(update.name())
                .filter(name -> !name.isBlank())
                .ifPresent(user::setName);
        Optional.ofNullable(update.email())
                .filter(email -> !email.isBlank() && !email.equals(user.getEmail()))
                .ifPresent(newEmail -> {
                    if (userRepository.findByEmail(newEmail).isPresent()) {
                        throw new EmailAlreadyInUseException(); }
                    user.setEmail(newEmail);
                });
        Optional.ofNullable(update.password())
                .filter(password -> !password.isBlank())
                .ifPresent(password -> user.setPassword(passwordEncoder.encode(password)));

        userRepository.save(user);
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getUserRole());
    }

    @Transactional
    public UserDeactivatedMessage disableUser(User user) {
        user.setUserStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        return new UserDeactivatedMessage();
    }
}
