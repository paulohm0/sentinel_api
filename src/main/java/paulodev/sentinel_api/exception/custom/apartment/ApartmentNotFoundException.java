package paulodev.sentinel_api.exception.custom.apartment;

public class ApartmentNotFoundException extends RuntimeException {
    public ApartmentNotFoundException() {
        super("Apartamento não encontrado");
    }
}

