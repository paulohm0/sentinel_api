package paulodev.sentinel_api.modules.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import paulodev.sentinel_api.exception.custom.user.EmailAlreadyInUseException;
import paulodev.sentinel_api.modules.user.dto.UserRegisterRequest;
import paulodev.sentinel_api.modules.user.dto.UserResponse;
import paulodev.sentinel_api.modules.user.entity.User;
import paulodev.sentinel_api.modules.user.entity.UserRole;
import paulodev.sentinel_api.modules.user.repository.UserRepository;

import java.util.ArrayList;

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
        User newUser = new User();
        newUser.setName(request.name());
        newUser.setEmail(request.email());
        newUser.setPassword(encryptedPassword);
        newUser.setUserRole(UserRole.USER);
        newUser.setCondominiums(new ArrayList<>());

        userRepository.save(newUser);

        UserResponse response = new UserResponse(
                newUser.getId(),
                newUser.getName(),
                newUser.getEmail(),
                newUser.getUserRole());
        return response;
    }
}
