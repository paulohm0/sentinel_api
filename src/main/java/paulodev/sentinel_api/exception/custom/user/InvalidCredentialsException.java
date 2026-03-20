package paulodev.sentinel_api.exception.custom.user;

public class InvalidCredentialsException extends RuntimeException{
    InvalidCredentialsException() { super("Credenciais inválidas"); }
}
