package paulodev.sentinel_api.modules.apartment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import paulodev.sentinel_api.exception.custom.apartment.ApartmentAlreadyExistsException;
import paulodev.sentinel_api.exception.custom.apartment.ApartmentNotFoundException;
import paulodev.sentinel_api.exception.custom.condominium.CondominiumNotFoundException;
import paulodev.sentinel_api.modules.apartment.entity.Apartment;
import paulodev.sentinel_api.modules.apartment.entity.ApartmentStatus;
import paulodev.sentinel_api.modules.apartment.dto.ApartmentCreateRequest;
import paulodev.sentinel_api.modules.apartment.dto.ApartmentDeactivatedMessage;
import paulodev.sentinel_api.modules.apartment.dto.ApartmentResponse;
import paulodev.sentinel_api.modules.apartment.dto.ApartmentUpdateRequest;
import paulodev.sentinel_api.modules.apartment.repository.ApartmentRepository;
import paulodev.sentinel_api.modules.condominium.entity.Condominium;
import paulodev.sentinel_api.modules.condominium.repository.CondominiumRepository;
import paulodev.sentinel_api.modules.user.entity.User;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApartmentService {

    private final ApartmentRepository apartmentRepository;
    private final CondominiumRepository condominiumRepository;

    public List<ApartmentResponse> getAllApartments() {
        List<Apartment> apartments = apartmentRepository.findAll();
        return apartments
                .stream()
                .map(ApartmentResponse::new)
                .toList();
    }

    public ApartmentResponse getApartmentById(UUID id, User authenticatedUser) {
        Apartment apartment = apartmentRepository.findByIdAndUser(id, authenticatedUser.getId())
                .orElseThrow(ApartmentNotFoundException::new);
        return new ApartmentResponse(apartment);
    }

    public List<ApartmentResponse> getApartmentsByCondominium(UUID condominiumId, User authenticatedUser) {
        condominiumRepository.findByIdAndUser(condominiumId, authenticatedUser.getId())
                .orElseThrow(CondominiumNotFoundException::new);

        List<Apartment> apartments = apartmentRepository.findByCondominiumId(condominiumId);
        return apartments
                .stream()
                .map(ApartmentResponse::new)
                .toList();
    }

    @Transactional
    public ApartmentResponse createApartment(ApartmentCreateRequest request, User authenticatedUser) {
        Condominium condominium = condominiumRepository.findByIdAndUser(request.condominiumId(), authenticatedUser.getId())
                .orElseThrow(CondominiumNotFoundException::new);

        if (apartmentRepository.findByNumberAndCondominiumId(request.number(), condominium.getId()).isPresent()) {
            throw new ApartmentAlreadyExistsException();
        }

        Apartment newApartment = new Apartment(request.number(), condominium);
        Apartment savedApartment = apartmentRepository.save(newApartment);
        return new ApartmentResponse(savedApartment);
    }

    @Transactional
    public ApartmentResponse updateApartment(UUID id, ApartmentUpdateRequest request, User authenticatedUser) {
        Apartment apartment = apartmentRepository.findByIdAndUser(id, authenticatedUser.getId())
                .orElseThrow(ApartmentNotFoundException::new);

        if (!apartment.getNumber().equals(request.number()) &&
            apartmentRepository.findByNumberAndCondominiumId(request.number(), apartment.getCondominium().getId()).isPresent()) {
            throw new ApartmentAlreadyExistsException();
        }

        apartment.setNumber(request.number());
        apartmentRepository.save(apartment);
        return new ApartmentResponse(apartment);
    }

    @Transactional
    public ApartmentDeactivatedMessage disableApartment(UUID id, User authenticatedUser) {
        Apartment apartment = apartmentRepository.findByIdAndUser(id, authenticatedUser.getId())
                .orElseThrow(ApartmentNotFoundException::new);

        // TODO(contract): bloquear se existir Contract com status ACTIVE vinculado a este apartamento — depende de ContractRepository, que ainda não existe

        apartment.setApartmentStatus(ApartmentStatus.INACTIVE);
        apartmentRepository.save(apartment);
        return new ApartmentDeactivatedMessage();
    }
}
