package paulodev.sentinel_api.modules.condominium.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import paulodev.sentinel_api.exception.custom.condominium.CondominiumEmptyListException;
import paulodev.sentinel_api.exception.custom.condominium.CondominiumNotFoundException;
import paulodev.sentinel_api.modules.apartment.entity.Apartment;
import paulodev.sentinel_api.modules.condominium.dto.CondominiumRegisterRequest;
import paulodev.sentinel_api.modules.condominium.dto.CondominiumUpdateRequest;
import paulodev.sentinel_api.modules.condominium.entity.Condominium;
import paulodev.sentinel_api.modules.condominium.entity.CondominiumStatus;
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
class CondominiumServiceTest {

    @Mock
    private CondominiumRepository condominiumRepository;

    @InjectMocks
    private CondominiumService condominiumService;

    @Nested
    class Register {

        @Test
        void register_WithValidData_ShouldReturnCondominiumResponse() {
            var user = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var request = new CondominiumRegisterRequest("Cond Alpha", "Rua A, 100");

            when(condominiumRepository.save(any(Condominium.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            var result = condominiumService.register(request, user);

            assertEquals("Cond Alpha", result.name());
            assertEquals("Rua A, 100", result.address());
            verify(condominiumRepository, times(1)).save(any(Condominium.class));
        }

    }

    @Nested
    class GetCondominiumInfo {

        @Test
        void getCondominiumInfo_WithOwnedCondominium_ShouldReturnCondominiumResponse() {
            var user = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condominium = new Condominium("Cond Alpha", "Rua A, 100", user);
            var condominiumId = UUID.randomUUID();

            when(condominiumRepository.findByIdAndUser(condominiumId, user.getId()))
                    .thenReturn(Optional.of(condominium));

            var result = condominiumService.getCondominiumInfo(condominiumId, user);

            assertEquals("Cond Alpha", result.name());
            assertEquals("Rua A, 100", result.address());
        }

        @Test
        void getCondominiumInfo_WithNonExistentCondominium_ShouldThrowCondominiumNotFoundException() {
            var user = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condominiumId = UUID.randomUUID();

            when(condominiumRepository.findByIdAndUser(condominiumId, user.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(CondominiumNotFoundException.class,
                    () -> condominiumService.getCondominiumInfo(condominiumId, user));
        }

        @Test
        void getCondominiumInfo_WithCondominiumBelongingToAnotherUser_ShouldThrowCondominiumNotFoundException() {
            var authenticatedUser = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condominiumId = UUID.randomUUID();

            // condominium existe, mas pertence a outro usuário: findByIdAndUser filtra e não retorna nada
            when(condominiumRepository.findByIdAndUser(condominiumId, authenticatedUser.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(CondominiumNotFoundException.class,
                    () -> condominiumService.getCondominiumInfo(condominiumId, authenticatedUser));
        }

    }

    @Nested
    class GetCondominiumListByUser {

        @Test
        void getCondominiumListByUser_WithNonEmptyList_ShouldReturnCondominiumResponseList() {
            var user = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condo1 = new Condominium("Cond Alpha", "Rua A, 100", user);
            var condo2 = new Condominium("Cond Beta", "Rua B, 200", user);

            when(condominiumRepository.findByUser(user.getId()))
                    .thenReturn(Optional.of(List.of(condo1, condo2)));

            var result = condominiumService.getCondominiumListByUser(user);

            assertEquals(2, result.size());
            assertEquals("Cond Alpha", result.get(0).name());
        }

        @Test
        void getCondominiumListByUser_WithEmptyList_ShouldThrowCondominiumEmptyListException() {
            var user = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);

            when(condominiumRepository.findByUser(user.getId()))
                    .thenReturn(Optional.of(List.of()));

            assertThrows(CondominiumEmptyListException.class,
                    () -> condominiumService.getCondominiumListByUser(user));
        }

        @Test
        void getCondominiumListByUser_WithEmptyOptional_ShouldThrowCondominiumEmptyListException() {
            var user = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);

            when(condominiumRepository.findByUser(user.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(CondominiumEmptyListException.class,
                    () -> condominiumService.getCondominiumListByUser(user));
        }

    }

    @Nested
    class ListCondominiumApartments {

        @Test
        void listCondominiumApartments_WithOwnedCondominium_ShouldReturnDetailsWithApartments() {
            var user = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condominium = new Condominium("Cond Alpha", "Rua A, 100", user);
            var apartment = new Apartment("101", condominium);
            condominium.getApartments().add(apartment);
            var condominiumId = UUID.randomUUID();

            when(condominiumRepository.findByIdAndUser(condominiumId, user.getId()))
                    .thenReturn(Optional.of(condominium));

            var result = condominiumService.listCondominiumApartments(condominiumId, user);

            assertEquals(1, result.totalApartments());
            assertEquals("101", result.apartments().get(0).number());
        }

        @Test
        void listCondominiumApartments_WithCondominiumBelongingToAnotherUser_ShouldThrowCondominiumNotFoundException() {
            var authenticatedUser = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condominiumId = UUID.randomUUID();

            when(condominiumRepository.findByIdAndUser(condominiumId, authenticatedUser.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(CondominiumNotFoundException.class,
                    () -> condominiumService.listCondominiumApartments(condominiumId, authenticatedUser));
        }

    }

    @Nested
    class UpdateCondominium {

        @Test
        void updateCondominium_WithOwnedCondominium_ShouldUpdateNameAndAddress() {
            var user = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condominium = new Condominium("Cond Alpha", "Rua A, 100", user);
            var condominiumId = UUID.randomUUID();
            var request = new CondominiumUpdateRequest("Cond Alpha Renovado", "Rua Nova, 200");

            when(condominiumRepository.findByIdAndUser(condominiumId, user.getId()))
                    .thenReturn(Optional.of(condominium));

            var result = condominiumService.updateCondominium(condominiumId, request, user);

            assertEquals("Cond Alpha Renovado", result.name());
            assertEquals("Rua Nova, 200", result.address());
            verify(condominiumRepository, times(1)).save(condominium);
        }

        @Test
        void updateCondominium_WithCondominiumBelongingToAnotherUser_ShouldThrowCondominiumNotFoundException() {
            var authenticatedUser = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condominiumId = UUID.randomUUID();
            var request = new CondominiumUpdateRequest("Novo Nome", "Novo Endereço");

            when(condominiumRepository.findByIdAndUser(condominiumId, authenticatedUser.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(CondominiumNotFoundException.class,
                    () -> condominiumService.updateCondominium(condominiumId, request, authenticatedUser));
            verify(condominiumRepository, never()).save(any());
        }

    }

    @Nested
    class DisableCondominium {

        @Test
        void disableCondominium_WithOwnedCondominium_ShouldSetInactiveAndReturnMessage() {
            var user = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condominium = new Condominium("Cond Alpha", "Rua A, 100", user);
            var condominiumId = UUID.randomUUID();

            when(condominiumRepository.findByIdAndUser(condominiumId, user.getId()))
                    .thenReturn(Optional.of(condominium));

            var result = condominiumService.disableCondominium(condominiumId, user);

            assertNotNull(result);
            assertEquals(CondominiumStatus.INACTIVE, condominium.getCondominiumStatus());
            verify(condominiumRepository, times(1)).save(condominium);
        }

        @Test
        void disableCondominium_WithCondominiumBelongingToAnotherUser_ShouldThrowCondominiumNotFoundException() {
            var authenticatedUser = new User("Paulo", "paulo@test.com", "123456", UserRole.USER);
            var condominiumId = UUID.randomUUID();

            when(condominiumRepository.findByIdAndUser(condominiumId, authenticatedUser.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(CondominiumNotFoundException.class,
                    () -> condominiumService.disableCondominium(condominiumId, authenticatedUser));
            verify(condominiumRepository, never()).save(any());
        }

    }

}
