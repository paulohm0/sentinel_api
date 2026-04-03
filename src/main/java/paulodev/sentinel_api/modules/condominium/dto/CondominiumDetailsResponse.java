package paulodev.sentinel_api.modules.condominium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import paulodev.sentinel_api.modules.apartment.dto.ApartmentResponse;
import paulodev.sentinel_api.modules.condominium.entity.Condominium;
import java.util.List;
import java.util.UUID;

public record CondominiumDetailsResponse(
        @JsonProperty("condominiumId")
        UUID condominiumId,
        @JsonProperty("name")
        String name,
        @JsonProperty("address")
        String address,
        @JsonProperty("totalApartments")
        int totalApartments,
        @JsonProperty("apartments")
        List<ApartmentResponse> apartments,
        @JsonProperty("message")
        String message
) {

    public CondominiumDetailsResponse(Condominium condominium) {
        this(condominium.getId(),
             condominium.getName(),
             condominium.getAddress(),
             condominium.getApartments().size(),
             condominium.getApartments()
                     .stream()
                     .map(ApartmentResponse::new)
                     .toList(),
             null);
    }

    public CondominiumDetailsResponse(Condominium condominium, String message) {
        this(condominium.getId(),
             condominium.getName(),
             condominium.getAddress(),
             condominium.getApartments().size(),
             condominium.getApartments()
                     .stream()
                     .map(ApartmentResponse::new)
                     .toList(),
             message);
    }
}

