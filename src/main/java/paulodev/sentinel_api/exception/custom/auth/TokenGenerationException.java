package paulodev.sentinel_api.exception.custom.auth;

public class TokenGenerationException extends RuntimeException {
    public TokenGenerationException() {
        super("Erro na geração do token");
    }
}
