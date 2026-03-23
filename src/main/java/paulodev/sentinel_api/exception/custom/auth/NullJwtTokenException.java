package paulodev.sentinel_api.exception.custom.auth;

public class NullJwtTokenException extends RuntimeException {
    public NullJwtTokenException() {
        super("Token de autenticação não informado");
    }
}
