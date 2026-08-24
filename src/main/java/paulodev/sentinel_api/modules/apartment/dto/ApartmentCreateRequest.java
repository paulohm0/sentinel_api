package paulodev.sentinel_api.modules.apartment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ApartmentCreateRequest(
        @NotBlank(message = "O número do apartamento não pode estar em branco")
        String number,
        @NotNull(message = "O ID do condomínio é obrigatório")
        UUID condominiumId
) { }

