package paulodev.sentinel_api.modules.apartment.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import paulodev.sentinel_api.exception.custom.apartment.ApartmentAlreadyExistsException;
import paulodev.sentinel_api.exception.custom.apartment.ApartmentNotFoundException;
import paulodev.sentinel_api.exception.custom.condominium.CondominiumNotFoundException;
import paulodev.sentinel_api.modules.apartment.dto.ApartmentCreateRequest;
import paulodev.sentinel_api.modules.apartment.dto.ApartmentUpdateRequest;
import paulodev.sentinel_api.modules.apartment.entity.Apartment;
import paulodev.sentinel_api.modules.apartment.entity.ApartmentStatus;
import paulodev.sentinel_api.modules.apartment.repository.ApartmentRepository;
import paulodev.sentinel_api.modules.condominium.entity.Condominium;
import paulodev.sentinel_api.modules.condominium.repository.CondominiumRepository;
import paulodev.sentinel_api.modules.user.entity.User;
import paulodev.sentinel_api.modules.user.entity.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApartmentServiceTest {

    @Mock
    private ApartmentRepository apartmentRepository;

    @Mock
    private CondominiumRepository condominiumRepository;

    @InjectMocks
    private ApartmentService apartmentService;

    @Nested
    class GetAllApartments {

        @Test
        void getAllApartments_WithApartments_ShouldReturnApartmentResponseList() {
            var user = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condominium = new Condominium("Cond Alpha", "Rua A, 100", user);
            var apartment1 = new Apartment("101", condominium);
            var apartment2 = new Apartment("102", condominium);

            when(apartmentRepository.findAll()).thenReturn(List.of(apartment1, apartment2));

            var result = apartmentService.getAllApartments();

            assertEquals(2, result.size());
            assertEquals("101", result.get(0).number());
        }

        @Test
        void getAllApartments_WithNoApartments_ShouldReturnEmptyList() {
            when(apartmentRepository.findAll()).thenReturn(List.of());

            var result = apartmentService.getAllApartments();

            assertTrue(result.isEmpty());
        }

    }

    @Nested
    class GetApartmentById {

        @Test
        void getApartmentById_WithOwnedApartment_ShouldReturnApartmentResponse() {
            var user = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condominium = new Condominium("Cond Alpha", "Rua A, 100", user);
            var apartment = new Apartment("101", condominium);
            var apartmentId = UUID.randomUUID();

            when(apartmentRepository.findByIdAndUser(apartmentId, user.getId()))
                    .thenReturn(Optional.of(apartment));

            var result = apartmentService.getApartmentById(apartmentId, user);

            assertEquals("101", result.number());
        }

        @Test
        void getApartmentById_WithNonExistentApartment_ShouldThrowApartmentNotFoundException() {
            var user = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var apartmentId = UUID.randomUUID();

            when(apartmentRepository.findByIdAndUser(apartmentId, user.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(ApartmentNotFoundException.class,
                    () -> apartmentService.getApartmentById(apartmentId, user));
        }

        @Test
        void getApartmentById_WithApartmentBelongingToAnotherUser_ShouldThrowApartmentNotFoundException() {
            var authenticatedUser = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var apartmentId = UUID.randomUUID();

            // apartamento existe, mas pertence a outro usuário: findByIdAndUser filtra e não retorna nada
            when(apartmentRepository.findByIdAndUser(apartmentId, authenticatedUser.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(ApartmentNotFoundException.class,
                    () -> apartmentService.getApartmentById(apartmentId, authenticatedUser));
        }

    }

    @Nested
    class GetApartmentsByCondominium {

        @Test
        void getApartmentsByCondominium_WithOwnedCondominium_ShouldReturnApartmentResponseList() {
            var user = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condominium = new Condominium("Cond Alpha", "Rua A, 100", user);
            var apartment = new Apartment("101", condominium);
            var condominiumId = UUID.randomUUID();

            when(condominiumRepository.findByIdAndUser(condominiumId, user.getId()))
                    .thenReturn(Optional.of(condominium));
            when(apartmentRepository.findByCondominiumId(condominiumId))
                    .thenReturn(List.of(apartment));

            var result = apartmentService.getApartmentsByCondominium(condominiumId, user);

            assertEquals(1, result.size());
            assertEquals("101", result.get(0).number());
        }

        @Test
        void getApartmentsByCondominium_WithCondominiumBelongingToAnotherUser_ShouldThrowCondominiumNotFoundException() {
            var authenticatedUser = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condominiumId = UUID.randomUUID();

            when(condominiumRepository.findByIdAndUser(condominiumId, authenticatedUser.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(CondominiumNotFoundException.class,
                    () -> apartmentService.getApartmentsByCondominium(condominiumId, authenticatedUser));
            verify(apartmentRepository, never()).findByCondominiumId(any());
        }

        @Test
        void getApartmentsByCondominium_WithNonExistentCondominium_ShouldThrowCondominiumNotFoundException() {
            var authenticatedUser = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condominiumId = UUID.randomUUID();

            when(condominiumRepository.findByIdAndUser(condominiumId, authenticatedUser.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(CondominiumNotFoundException.class,
                    () -> apartmentService.getApartmentsByCondominium(condominiumId, authenticatedUser));
        }

    }

    @Nested
    class CreateApartment {

        @Test
        void createApartment_WithValidData_ShouldReturnApartmentResponse() {
            var user = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condominium = new Condominium("Cond Alpha", "Rua A, 100", user);
            var condominiumId = UUID.randomUUID();
            var request = new ApartmentCreateRequest("101", condominiumId);

            when(condominiumRepository.findByIdAndUser(condominiumId, user.getId()))
                    .thenReturn(Optional.of(condominium));
            when(apartmentRepository.findByNumberAndCondominiumId("101", condominium.getId()))
                    .thenReturn(Optional.empty());
            when(apartmentRepository.save(any(Apartment.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            var result = apartmentService.createApartment(request, user);

            assertEquals("101", result.number());
            verify(apartmentRepository, times(1)).save(any(Apartment.class));
        }

        @Test
        void createApartment_WithCondominiumBelongingToAnotherUser_ShouldThrowCondominiumNotFoundException() {
            var authenticatedUser = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condominiumId = UUID.randomUUID();
            var request = new ApartmentCreateRequest("101", condominiumId);

            when(condominiumRepository.findByIdAndUser(condominiumId, authenticatedUser.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(CondominiumNotFoundException.class,
                    () -> apartmentService.createApartment(request, authenticatedUser));
            verify(apartmentRepository, never()).save(any());
        }

        @Test
        void createApartment_WithDuplicateNumberInSameCondominium_ShouldThrowApartmentAlreadyExistsException() {
            var user = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condominium = new Condominium("Cond Alpha", "Rua A, 100", user);
            var condominiumId = UUID.randomUUID();
            var request = new ApartmentCreateRequest("101", condominiumId);
            var existingApartment = new Apartment("101", condominium);

            when(condominiumRepository.findByIdAndUser(condominiumId, user.getId()))
                    .thenReturn(Optional.of(condominium));
            when(apartmentRepository.findByNumberAndCondominiumId("101", condominium.getId()))
                    .thenReturn(Optional.of(existingApartment));

            assertThrows(ApartmentAlreadyExistsException.class,
                    () -> apartmentService.createApartment(request, user));
            verify(apartmentRepository, never()).save(any());
        }

        @Test
        void createApartment_WithSameNumberInDifferentCondominium_ShouldReturnApartmentResponse() {
            // Mesmo número "101" já existe em OUTRO condomínio: a duplicidade é escopada por
            // condomínio (findByNumberAndCondominiumId), então isso NÃO deve gerar conflito.
            var user = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condominium = new Condominium("Cond Beta", "Rua B, 200", user);
            var condominiumId = UUID.randomUUID();
            var request = new ApartmentCreateRequest("101", condominiumId);

            when(condominiumRepository.findByIdAndUser(condominiumId, user.getId()))
                    .thenReturn(Optional.of(condominium));
            // não há apartamento "101" NESTE condomínio (existe em outro, mas isso é irrelevante aqui)
            when(apartmentRepository.findByNumberAndCondominiumId("101", condominium.getId()))
                    .thenReturn(Optional.empty());
            when(apartmentRepository.save(any(Apartment.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            var result = apartmentService.createApartment(request, user);

            assertEquals("101", result.number());
            verify(apartmentRepository, times(1)).save(any(Apartment.class));
        }

    }

    @Nested
    class UpdateApartment {

        @Test
        void updateApartment_WithOwnedApartment_ShouldUpdateNumber() {
            var user = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condominium = new Condominium("Cond Alpha", "Rua A, 100", user);
            var apartment = new Apartment("101", condominium);
            var apartmentId = UUID.randomUUID();
            var request = new ApartmentUpdateRequest("102");

            when(apartmentRepository.findByIdAndUser(apartmentId, user.getId()))
                    .thenReturn(Optional.of(apartment));
            when(apartmentRepository.findByNumberAndCondominiumId("102", condominium.getId()))
                    .thenReturn(Optional.empty());

            var result = apartmentService.updateApartment(apartmentId, request, user);

            assertEquals("102", result.number());
            verify(apartmentRepository, times(1)).save(apartment);
        }

        @Test
        void updateApartment_WithApartmentBelongingToAnotherUser_ShouldThrowApartmentNotFoundException() {
            var authenticatedUser = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var apartmentId = UUID.randomUUID();
            var request = new ApartmentUpdateRequest("102");

            when(apartmentRepository.findByIdAndUser(apartmentId, authenticatedUser.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(ApartmentNotFoundException.class,
                    () -> apartmentService.updateApartment(apartmentId, request, authenticatedUser));
            verify(apartmentRepository, never()).save(any());
        }

        @Test
        void updateApartment_WithDuplicateNumberInSameCondominium_ShouldThrowApartmentAlreadyExistsException() {
            var user = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condominium = new Condominium("Cond Alpha", "Rua A, 100", user);
            var apartment = new Apartment("101", condominium);
            var otherApartment = new Apartment("102", condominium);
            var apartmentId = UUID.randomUUID();
            var request = new ApartmentUpdateRequest("102");

            when(apartmentRepository.findByIdAndUser(apartmentId, user.getId()))
                    .thenReturn(Optional.of(apartment));
            when(apartmentRepository.findByNumberAndCondominiumId("102", condominium.getId()))
                    .thenReturn(Optional.of(otherApartment));

            assertThrows(ApartmentAlreadyExistsException.class,
                    () -> apartmentService.updateApartment(apartmentId, request, user));
            verify(apartmentRepository, never()).save(any());
        }

        @Test
        void updateApartment_WithSameNumberAsCurrent_ShouldNotThrowAndKeepNumber() {
            var user = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condominium = new Condominium("Cond Alpha", "Rua A, 100", user);
            var apartment = new Apartment("101", condominium);
            var apartmentId = UUID.randomUUID();
            var request = new ApartmentUpdateRequest("101");

            when(apartmentRepository.findByIdAndUser(apartmentId, user.getId()))
                    .thenReturn(Optional.of(apartment));

            var result = apartmentService.updateApartment(apartmentId, request, user);

            assertEquals("101", result.number());
            verify(apartmentRepository, never()).findByNumberAndCondominiumId(any(), any());
            verify(apartmentRepository, times(1)).save(apartment);
        }

        @Test
        void updateApartment_WithSameNumberUsedInDifferentCondominium_ShouldUpdateNumber() {
            // "202" já existe em outro condomínio, mas a checagem de duplicidade é escopada
            // pelo condomínio do apartamento sendo editado, então não deve haver conflito.
            var user = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condominium = new Condominium("Cond Alpha", "Rua A, 100", user);
            var apartment = new Apartment("101", condominium);
            var apartmentId = UUID.randomUUID();
            var request = new ApartmentUpdateRequest("202");

            when(apartmentRepository.findByIdAndUser(apartmentId, user.getId()))
                    .thenReturn(Optional.of(apartment));
            when(apartmentRepository.findByNumberAndCondominiumId("202", condominium.getId()))
                    .thenReturn(Optional.empty());

            var result = apartmentService.updateApartment(apartmentId, request, user);

            assertEquals("202", result.number());
            verify(apartmentRepository, times(1)).save(apartment);
        }

    }

    @Nested
    class DisableApartment {

        @Test
        void disableApartment_WithOwnedApartment_ShouldSetInactiveAndReturnMessage() {
            var user = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condominium = new Condominium("Cond Alpha", "Rua A, 100", user);
            var apartment = new Apartment("101", condominium);
            var apartmentId = UUID.randomUUID();

            when(apartmentRepository.findByIdAndUser(apartmentId, user.getId()))
                    .thenReturn(Optional.of(apartment));

            var result = apartmentService.disableApartment(apartmentId, user);

            assertNotNull(result);
            assertEquals(ApartmentStatus.INACTIVE, apartment.getApartmentStatus());
            verify(apartmentRepository, times(1)).save(apartment);
        }

        @Test
        void disableApartment_WithApartmentBelongingToAnotherUser_ShouldThrowApartmentNotFoundException() {
            var authenticatedUser = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var apartmentId = UUID.randomUUID();

            when(apartmentRepository.findByIdAndUser(apartmentId, authenticatedUser.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(ApartmentNotFoundException.class,
                    () -> apartmentService.disableApartment(apartmentId, authenticatedUser));
            verify(apartmentRepository, never()).save(any());
        }

    }

}
