package paulodev.sentinel_api.exception.custom.auth;

public class ExpiredJwtTokenException extends RuntimeException {
    public ExpiredJwtTokenException() {
        super("Token expirado");
    }
}
