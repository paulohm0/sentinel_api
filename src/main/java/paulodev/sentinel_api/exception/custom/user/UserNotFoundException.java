package paulodev.sentinel_api.exception.custom.user;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Usuário nao encontrado");
    }
}
