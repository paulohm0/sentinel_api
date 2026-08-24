package paulodev.sentinel_api.modules.condominium.dto;

public record CondominiumDeactivatedMessage(String message) {
    public CondominiumDeactivatedMessage() {
        this("Condomínio desativado com sucesso");
    }
}
