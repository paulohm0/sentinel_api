package paulodev.sentinel_api.modules.apartment.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record ApartmentUpdateRequest(
        @NotBlank(message = "O número do apartamento não pode estar em branco")
        String number
) { }

