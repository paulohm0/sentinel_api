package paulodev.sentinel_api.modules.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegisterRequest(
        @NotBlank String name,
        @NotBlank String password,
        @NotBlank @Email String email
) { }
