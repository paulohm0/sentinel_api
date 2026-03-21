package paulodev.sentinel_api.modules.auth.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import paulodev.sentinel_api.exception.handler.ErrorResponse;
import paulodev.sentinel_api.modules.auth.dto.LoginResponse;
import paulodev.sentinel_api.modules.user.dto.UserLoginRequest;

@Tag(name = "Autenticação", description = "Validação de credenciais e emissão de tokens")
public interface AuthDocApi {

    @Operation(summary = "Realizar login no sistema")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário logado com sucesso"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Erro de validação nos dados enviados (campos vazios ou email inválido)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "E-mail não cadastrado ou senha incorreta",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    ResponseEntity<LoginResponse> login(@Valid @RequestBody UserLoginRequest request);
}
