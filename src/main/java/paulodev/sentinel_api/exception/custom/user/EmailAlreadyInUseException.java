package paulodev.sentinel_api.exception.custom.user;

public class EmailAlreadyInUseException extends RuntimeException {
    EmailAlreadyInUseException() {
        super("Email ja cadastrado");
    }
}
