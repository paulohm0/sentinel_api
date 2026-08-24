package paulodev.sentinel_api.modules.apartment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import paulodev.sentinel_api.modules.apartment.entity.Apartment;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApartmentResponse(
        UUID id,
        String number,
        String message
) {

    public ApartmentResponse(Apartment apartment) {
        this(apartment.getId(), apartment.getNumber(), null);
    }

    public ApartmentResponse(Apartment apartment, String message) {
        this(apartment.getId(), apartment.getNumber(), message);
    }
}

