package paulodev.sentinel_api.modules.apartment.dto;

public record ApartmentDeactivatedMessage(String message) {
    public ApartmentDeactivatedMessage() {
        this("Apartamento desativado com sucesso");
    }
}
