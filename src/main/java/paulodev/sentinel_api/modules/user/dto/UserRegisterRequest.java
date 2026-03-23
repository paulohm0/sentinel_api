package paulodev.sentinel_api.modules.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import paulodev.sentinel_api.modules.user.entity.UserRole;

public record UserRegisterRequest(
        @NotBlank
        String name,
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        @NotBlank
        String password,
        @NotBlank
        @Email
        String email,
        @NotNull
        UserRole userRole
) { }
