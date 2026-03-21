package paulodev.sentinel_api.exception.custom.user;

public class EmailAlreadyInUseException extends RuntimeException {
    public EmailAlreadyInUseException() {
        super("Email ja cadastrado");
    }
}
