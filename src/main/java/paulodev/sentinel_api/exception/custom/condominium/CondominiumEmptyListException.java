package paulodev.sentinel_api.exception.custom.condominium;

public class CondominiumEmptyListException extends RuntimeException {
    public CondominiumEmptyListException() {
        super("Nenhum condomínio encontrado para este usuário");
    }
}

