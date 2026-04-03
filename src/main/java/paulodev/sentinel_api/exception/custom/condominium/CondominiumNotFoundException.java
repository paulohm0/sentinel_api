package paulodev.sentinel_api.exception.custom.condominium;

public class CondominiumNotFoundException extends RuntimeException {
    public CondominiumNotFoundException() {
        super("Condomínio não encontrado");
    }
}

