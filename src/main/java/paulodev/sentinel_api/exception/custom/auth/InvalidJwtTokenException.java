package paulodev.sentinel_api.exception.custom.auth;

public class InvalidJwtTokenException extends RuntimeException {
    public InvalidJwtTokenException() {
        super("Token Inválido");
    }
}
