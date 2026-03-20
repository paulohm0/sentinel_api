package paulodev.sentinel_api.modules.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import paulodev.sentinel_api.modules.user.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository  userRepository;
}
