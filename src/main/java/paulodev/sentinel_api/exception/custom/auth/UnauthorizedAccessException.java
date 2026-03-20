package paulodev.sentinel_api.exception.custom.auth;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException() {
        super("Acesso não autorizado");
    }
}
