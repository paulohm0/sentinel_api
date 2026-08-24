package paulodev.sentinel_api.exception.custom.apartment;

public class ApartmentAlreadyExistsException extends RuntimeException {
    public ApartmentAlreadyExistsException() {
        super("O número de apartamento já está em uso neste condomínio");
    }
}

