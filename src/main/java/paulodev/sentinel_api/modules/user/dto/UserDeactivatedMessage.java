package paulodev.sentinel_api.modules.user.dto;

public record UserDeactivatedMessage(String message) {
    public UserDeactivatedMessage() {
        this("Usuário desativado com sucesso");
    }
}
