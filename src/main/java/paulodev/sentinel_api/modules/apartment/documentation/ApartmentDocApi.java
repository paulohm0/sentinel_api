package paulodev.sentinel_api.modules.apartment.documentation;

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
import paulodev.sentinel_api.modules.apartment.dto.ApartmentCreateRequest;
import paulodev.sentinel_api.modules.apartment.dto.ApartmentDeactivatedMessage;
import paulodev.sentinel_api.modules.apartment.dto.ApartmentResponse;
import paulodev.sentinel_api.modules.apartment.dto.ApartmentUpdateRequest;
import paulodev.sentinel_api.modules.user.entity.User;

import java.util.List;
import java.util.UUID;

@Tag(name = "Apartamentos", description = "Gerenciamento de apartamentos")
public interface ApartmentDocApi {

    @Operation(summary = "Listar todos os apartamentos")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de apartamentos retornada com sucesso"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Erro de autenticação do usuário, token expirado ou inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    ResponseEntity<List<ApartmentResponse>> listAllApartments();

    @Operation(summary = "Criar novo apartamento")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Apartamento criado com sucesso"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Erro de validação nos dados enviados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "O número de apartamento já está em uso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Condomínio não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    ResponseEntity<ApartmentResponse> createApartment(
            @Valid @RequestBody ApartmentCreateRequest request,
            @AuthenticationPrincipal User authenticatedUser);

    @Operation(summary = "Obter apartamento por ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Apartamento encontrado com sucesso"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Apartamento não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    ResponseEntity<ApartmentResponse> getApartmentById(
            @PathVariable UUID apartmentId,
            @AuthenticationPrincipal User authenticatedUser);

    @Operation(summary = "Listar apartamentos por condomínio")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de apartamentos do condomínio retornada com sucesso"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Condomínio não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    ResponseEntity<List<ApartmentResponse>> getApartmentsByCondominium(
            @PathVariable UUID condominiumId,
            @AuthenticationPrincipal User authenticatedUser);

    @Operation(summary = "Atualizar apartamento")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Apartamento atualizado com sucesso"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Erro de validação nos dados enviados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Apartamento não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "O número de apartamento já está em uso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    ResponseEntity<ApartmentResponse> updateApartment(
            @PathVariable UUID apartmentId,
            @Valid @RequestBody ApartmentUpdateRequest request,
            @AuthenticationPrincipal User authenticatedUser);

    @Operation(summary = "Desativar apartamento", description = "Realiza uma desativação lógica (soft delete) do apartamento, não uma remoção definitiva")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Apartamento desativado com sucesso"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Apartamento não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    ResponseEntity<ApartmentDeactivatedMessage> deleteApartment(
            @PathVariable UUID apartmentId,
            @AuthenticationPrincipal User authenticatedUser);
}
