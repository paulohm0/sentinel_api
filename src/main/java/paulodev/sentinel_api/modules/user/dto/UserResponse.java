package paulodev.sentinel_api.modules.user.dto;

import paulodev.sentinel_api.modules.user.entity.UserRole;

import java.util.UUID;

public record UserResponse(
        UUID userId,
        String name,
        String email,
        UserRole userRole
) { }
