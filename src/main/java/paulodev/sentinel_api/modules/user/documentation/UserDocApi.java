package paulodev.sentinel_api.modules.user.documentation;

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
import paulodev.sentinel_api.exception.handler.ErrorResponse;
import paulodev.sentinel_api.modules.user.dto.UserDeactivatedMessage;
import paulodev.sentinel_api.modules.user.dto.UserRegisterRequest;
import paulodev.sentinel_api.modules.user.dto.UserResponse;
import paulodev.sentinel_api.modules.user.dto.UserUpdateInfoRequest;
import paulodev.sentinel_api.modules.user.entity.User;

import java.util.List;

@Tag(name = "Usuários", description = "Gerenciamento de usuários")
public interface UserDocApi {

    @Operation(summary = "Listar todos os usuários")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuários retornada com sucesso"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Erro de autenticação do usuário, token expirado ou inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    ResponseEntity<List<UserResponse>> listAllUsers();


    @Operation(summary = "Registrar um novo usuário")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário criado com sucesso"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Erro de validação nos dados enviados (campos vazios ou email inválido)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "O e-mail informado já está em uso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegisterRequest request);


    @Operation(summary = "Visualizar os dados do usuário")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Dados exibidos com sucesso"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Erro de autenticação do usuário, token expirado ou inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    ResponseEntity<UserResponse> getUserInfo(@AuthenticationPrincipal User user);


    @Operation(summary = "Atualizar informações do usuário")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Informações do usuário atualizadas com sucesso"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Erro de validação nos dados enviados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Erro de autenticação do usuário, token expirado ou inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    ResponseEntity<UserResponse> updateUserInfo(@AuthenticationPrincipal User authenticatedUser, @Valid @RequestBody UserUpdateInfoRequest update);


    @Operation(summary = "Desativar usuário")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário desativado com sucesso"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Erro de autenticação do usuário, token expirado ou inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    ResponseEntity<UserDeactivatedMessage> disableUser(@AuthenticationPrincipal User authenticatedUser);
}
