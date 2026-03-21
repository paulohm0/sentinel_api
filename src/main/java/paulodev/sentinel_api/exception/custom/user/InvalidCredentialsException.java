package paulodev.sentinel_api.exception.custom.user;

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException() {
        super("Credenciais inválidas");
    }
}
