package paulodev.sentinel_api.modules.condominium.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import paulodev.sentinel_api.exception.handler.ErrorResponse;
import paulodev.sentinel_api.modules.condominium.dto.CondominiumRegisterRequest;
import paulodev.sentinel_api.modules.condominium.dto.CondominiumResponse;
import paulodev.sentinel_api.modules.condominium.dto.CondominiumDetailsResponse;
import paulodev.sentinel_api.modules.user.entity.User;

import java.util.List;
import java.util.UUID;

@Tag(name = "Condomínios", description = "Gerenciamento de condomínios")
public interface CondominiumDocApi {

    @Operation(summary = "Registrar um novo condomínio")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Condomínio criado com sucesso"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Erro de validação nos dados enviados (campos vazios)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Erro de autenticação do usuário, token expirado ou inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    ResponseEntity<CondominiumResponse> register(
            @Valid @RequestBody CondominiumRegisterRequest request,
            @AuthenticationPrincipal User authenticatedUser);

    @Operation(summary = "Listar todos os condomínios do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de condomínios retornada com sucesso"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Erro de autenticação do usuário, token expirado ou inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    ResponseEntity<List<CondominiumResponse>> getCondominiumListByUser(
            @AuthenticationPrincipal User authenticatedUser);

    @Operation(summary = "Obter informações do condomínio")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Informações do condomínio retornadas com sucesso"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Erro de autenticação do usuário, token expirado ou inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Condomínio não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    ResponseEntity<CondominiumResponse> getCondominiumSummary(
            @PathVariable UUID condominiumId,
            @AuthenticationPrincipal User authenticatedUser);

    @Operation(summary = "Listar todos os apartamentos do condomínio")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de apartamentos retornada com sucesso"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Erro de autenticação do usuário, token expirado ou inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Condomínio não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    ResponseEntity<CondominiumDetailsResponse> getCondominiumDetails(
            @PathVariable UUID condominiumId,
            @AuthenticationPrincipal User authenticatedUser);
}

