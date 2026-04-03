package paulodev.sentinel_api.modules.condominium.dto;

import jakarta.validation.constraints.NotBlank;

public record CondominiumRegisterRequest(
        @NotBlank
        String name,
        @NotBlank
        String address
) { }
