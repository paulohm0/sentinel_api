package paulodev.sentinel_api.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import paulodev.sentinel_api.exception.custom.auth.ExpiredJwtTokenException;
import paulodev.sentinel_api.exception.custom.auth.InvalidJwtTokenException;
import paulodev.sentinel_api.exception.custom.auth.TokenGenerationException;
import paulodev.sentinel_api.modules.user.entity.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secretToken;

    public String tokenGenerate(User user) {
        try {
            Algorithm algorithm  = Algorithm.HMAC256(secretToken);
            return JWT.create()
                    .withIssuer("sentinel_api")
                    .withSubject(user.getUsername())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new TokenGenerationException();
        }
    }

    public String tokenValidate(String token) {  // Devolve o email se estiver tudo ok
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretToken);
            return JWT.require(algorithm)
                    .withIssuer("sentinel_api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (TokenExpiredException exception) {
            throw new ExpiredJwtTokenException();
        } catch (JWTVerificationException exception) {
            throw new InvalidJwtTokenException();
        }
    }

    // DEFINE EXPIRAÇÃO DO TOKEN EM 2 HRS
    private Instant genExpirationDate(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
