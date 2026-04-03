package paulodev.sentinel_api.modules.condominium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import paulodev.sentinel_api.modules.condominium.entity.Condominium;
import java.util.UUID;

public record CondominiumResponse(
        @JsonProperty("condominiumId")
        UUID condominiumId,
        @JsonProperty("name")
        String name,
        @JsonProperty("address")
        String address,
        @JsonProperty("totalApartments")
        int totalApartments,
        @JsonProperty("message")
        String message
) {

    public CondominiumResponse(Condominium condominium) {
        this(condominium.getId(),
             condominium.getName(),
             condominium.getAddress(),
             condominium.getApartments().size(),
             null);
    }

    public CondominiumResponse(Condominium condominium, String message) {
        this(condominium.getId(),
             condominium.getName(),
             condominium.getAddress(),
             condominium.getApartments().size(),
             message);
    }
}
