package paulodev.sentinel_api.modules.condominium.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import paulodev.sentinel_api.modules.condominium.documentation.CondominiumDocApi;
import paulodev.sentinel_api.modules.condominium.dto.CondominiumRegisterRequest;
import paulodev.sentinel_api.modules.condominium.dto.CondominiumResponse;
import paulodev.sentinel_api.modules.condominium.dto.CondominiumDetailsResponse;
import paulodev.sentinel_api.modules.condominium.service.CondominiumService;
import paulodev.sentinel_api.modules.user.entity.User;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/condominium")
public class CondominiumController implements CondominiumDocApi {

    private final CondominiumService condominiumService;

    @PostMapping("/register")
    public ResponseEntity<CondominiumResponse> register(
            @Valid @RequestBody CondominiumRegisterRequest request,
            @AuthenticationPrincipal User authenticatedUser)
    {
        var response = condominiumService.register(request, authenticatedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/list")
    public ResponseEntity<List<CondominiumResponse>> getCondominiumListByUser(
            @AuthenticationPrincipal User authenticatedUser)
    {
        var response = condominiumService.getCondominiumListByUser(authenticatedUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary/{condominiumId}")
    public ResponseEntity<CondominiumResponse> getCondominiumSummary(
            @PathVariable UUID condominiumId,
            @AuthenticationPrincipal User authenticatedUser)
    {
        var response = condominiumService.getCondominiumInfo(condominiumId,authenticatedUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/details/{condominiumId}")
    public ResponseEntity<CondominiumDetailsResponse> getCondominiumDetails(
            @PathVariable UUID condominiumId,
            @AuthenticationPrincipal User authenticatedUser)
    {
        var response = condominiumService.listCondominiumApartments(condominiumId,authenticatedUser);
        return ResponseEntity.ok(response);
    }
}
