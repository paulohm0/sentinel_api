package paulodev.sentinel_api.modules.condominium.dto;

import jakarta.validation.constraints.NotBlank;

public record CondominiumUpdateRequest(
        @NotBlank(message = "O nome do condomínio não pode estar em branco")
        String name,
        @NotBlank(message = "O endereço do condomínio não pode estar em branco")
        String address
) { }
