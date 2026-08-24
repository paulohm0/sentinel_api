package paulodev.sentinel_api.modules.apartment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import paulodev.sentinel_api.modules.apartment.documentation.ApartmentDocApi;
import paulodev.sentinel_api.modules.apartment.dto.ApartmentCreateRequest;
import paulodev.sentinel_api.modules.apartment.dto.ApartmentDeactivatedMessage;
import paulodev.sentinel_api.modules.apartment.dto.ApartmentResponse;
import paulodev.sentinel_api.modules.apartment.dto.ApartmentUpdateRequest;
import paulodev.sentinel_api.modules.apartment.service.ApartmentService;
import paulodev.sentinel_api.modules.user.entity.User;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/apartment")
public class ApartmentController implements ApartmentDocApi {

    private final ApartmentService apartmentService;

    /// ADMIN

    @GetMapping("/list")
    public ResponseEntity<List<ApartmentResponse>> listAllApartments()
    {
        var response = apartmentService.getAllApartments();
        return ResponseEntity.ok(response);
    }

    /// USER

    @PostMapping("/create")
    public ResponseEntity<ApartmentResponse> createApartment(
            @Valid @RequestBody ApartmentCreateRequest request,
            @AuthenticationPrincipal User authenticatedUser)
    {
        var response = apartmentService.createApartment(request, authenticatedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/info/{apartmentId}")
    public ResponseEntity<ApartmentResponse> getApartmentById(
            @PathVariable UUID apartmentId,
            @AuthenticationPrincipal User authenticatedUser)
    {
        var response = apartmentService.getApartmentById(apartmentId, authenticatedUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list/condominium/{condominiumId}")
    public ResponseEntity<List<ApartmentResponse>> getApartmentsByCondominium(
            @PathVariable UUID condominiumId,
            @AuthenticationPrincipal User authenticatedUser)
    {
        var response = apartmentService.getApartmentsByCondominium(condominiumId, authenticatedUser);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update/{apartmentId}")
    public ResponseEntity<ApartmentResponse> updateApartment(
            @PathVariable UUID apartmentId,
            @Valid @RequestBody ApartmentUpdateRequest request,
            @AuthenticationPrincipal User authenticatedUser)
    {
        var response = apartmentService.updateApartment(apartmentId, request, authenticatedUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{apartmentId}")
    public ResponseEntity<ApartmentDeactivatedMessage> deleteApartment(
            @PathVariable UUID apartmentId,
            @AuthenticationPrincipal User authenticatedUser)
    {
        var response = apartmentService.disableApartment(apartmentId, authenticatedUser);
        return ResponseEntity.ok(response);
    }
}
