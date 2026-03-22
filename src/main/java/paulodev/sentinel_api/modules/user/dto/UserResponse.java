package paulodev.sentinel_api.modules.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import paulodev.sentinel_api.modules.user.entity.UserRole;

import java.util.UUID;
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(
        UUID userId,
        String name,
        String email,
        UserRole userRole,
        String message
) { }
