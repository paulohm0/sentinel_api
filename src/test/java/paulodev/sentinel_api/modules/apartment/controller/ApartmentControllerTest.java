package paulodev.sentinel_api.modules.apartment.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import paulodev.sentinel_api.config.security.TokenService;
import paulodev.sentinel_api.exception.custom.apartment.ApartmentAlreadyExistsException;
import paulodev.sentinel_api.exception.custom.apartment.ApartmentNotFoundException;
import paulodev.sentinel_api.exception.custom.condominium.CondominiumNotFoundException;
import paulodev.sentinel_api.modules.apartment.dto.ApartmentCreateRequest;
import paulodev.sentinel_api.modules.apartment.dto.ApartmentDeactivatedMessage;
import paulodev.sentinel_api.modules.apartment.dto.ApartmentResponse;
import paulodev.sentinel_api.modules.apartment.dto.ApartmentUpdateRequest;
import paulodev.sentinel_api.modules.apartment.service.ApartmentService;
import paulodev.sentinel_api.modules.user.entity.User;
import paulodev.sentinel_api.modules.user.entity.UserRole;
import paulodev.sentinel_api.modules.user.repository.UserRepository;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ApartmentController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class}
)
class ApartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ApartmentService apartmentService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    @Nested
    class ListAllApartments {

        @Test
        void withApartments() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var response = List.of(
                    new ApartmentResponse(UUID.randomUUID(), "101", null),
                    new ApartmentResponse(UUID.randomUUID(), "102", null));

            when(apartmentService.getAllApartments()).thenReturn(response);

            mockMvc.perform(get("/apartment/list")
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()", is(2)))
                    .andExpect(jsonPath("$[0].number", is("101")));
        }

    }

    @Nested
    class CreateApartment {

        @Test
        void withValidRequest() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var condominiumId = UUID.randomUUID();
            var request = new ApartmentCreateRequest("101", condominiumId);
            var response = new ApartmentResponse(UUID.randomUUID(), "101", null);

            when(apartmentService.createApartment(eq(request), any(User.class))).thenReturn(response);

            mockMvc.perform(post("/apartment/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.number", is("101")));
        }

        @Test
        void withBlankNumber() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var request = new ApartmentCreateRequest("", UUID.randomUUID());

            mockMvc.perform(post("/apartment/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void withCondominiumBelongingToAnotherUser_ShouldReturnNotFound() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var request = new ApartmentCreateRequest("101", UUID.randomUUID());

            when(apartmentService.createApartment(eq(request), any(User.class)))
                    .thenThrow(new CondominiumNotFoundException());

            mockMvc.perform(post("/apartment/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void withDuplicateNumber_ShouldReturnConflict() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var request = new ApartmentCreateRequest("101", UUID.randomUUID());

            when(apartmentService.createApartment(eq(request), any(User.class)))
                    .thenThrow(new ApartmentAlreadyExistsException());

            mockMvc.perform(post("/apartment/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isConflict());
        }

    }

    @Nested
    class GetApartmentById {

        @Test
        void withOwnedApartment() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var apartmentId = UUID.randomUUID();
            var response = new ApartmentResponse(apartmentId, "101", null);

            when(apartmentService.getApartmentById(eq(apartmentId), any(User.class))).thenReturn(response);

            mockMvc.perform(get("/apartment/info/{apartmentId}", apartmentId)
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.number", is("101")));
        }

        @Test
        void withApartmentBelongingToAnotherUser_ShouldReturnNotFound() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var apartmentId = UUID.randomUUID();

            when(apartmentService.getApartmentById(eq(apartmentId), any(User.class)))
                    .thenThrow(new ApartmentNotFoundException());

            mockMvc.perform(get("/apartment/info/{apartmentId}", apartmentId)
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isNotFound());
        }

    }

    @Nested
    class GetApartmentsByCondominium {

        @Test
        void withOwnedCondominium() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var condominiumId = UUID.randomUUID();
            var response = List.of(new ApartmentResponse(UUID.randomUUID(), "101", null));

            when(apartmentService.getApartmentsByCondominium(eq(condominiumId), any(User.class)))
                    .thenReturn(response);

            mockMvc.perform(get("/apartment/list/condominium/{condominiumId}", condominiumId)
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()", is(1)))
                    .andExpect(jsonPath("$[0].number", is("101")));
        }

        @Test
        void withCondominiumBelongingToAnotherUser_ShouldReturnNotFound() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var condominiumId = UUID.randomUUID();

            when(apartmentService.getApartmentsByCondominium(eq(condominiumId), any(User.class)))
                    .thenThrow(new CondominiumNotFoundException());

            mockMvc.perform(get("/apartment/list/condominium/{condominiumId}", condominiumId)
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isNotFound());
        }

    }

    @Nested
    class UpdateApartment {

        @Test
        void withValidRequest() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var apartmentId = UUID.randomUUID();
            var request = new ApartmentUpdateRequest("102");
            var response = new ApartmentResponse(apartmentId, "102", null);

            when(apartmentService.updateApartment(eq(apartmentId), eq(request), any(User.class)))
                    .thenReturn(response);

            mockMvc.perform(patch("/apartment/update/{apartmentId}", apartmentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.number", is("102")));
        }

        @Test
        void withBlankNumber() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var apartmentId = UUID.randomUUID();
            var request = new ApartmentUpdateRequest("");

            mockMvc.perform(patch("/apartment/update/{apartmentId}", apartmentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void withApartmentBelongingToAnotherUser_ShouldReturnNotFound() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var apartmentId = UUID.randomUUID();
            var request = new ApartmentUpdateRequest("102");

            when(apartmentService.updateApartment(eq(apartmentId), eq(request), any(User.class)))
                    .thenThrow(new ApartmentNotFoundException());

            mockMvc.perform(patch("/apartment/update/{apartmentId}", apartmentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void withDuplicateNumber_ShouldReturnConflict() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var apartmentId = UUID.randomUUID();
            var request = new ApartmentUpdateRequest("102");

            when(apartmentService.updateApartment(eq(apartmentId), eq(request), any(User.class)))
                    .thenThrow(new ApartmentAlreadyExistsException());

            mockMvc.perform(patch("/apartment/update/{apartmentId}", apartmentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isConflict());
        }

    }

    @Nested
    class DeleteApartment {

        @Test
        void withOwnedApartment() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var apartmentId = UUID.randomUUID();
            var response = new ApartmentDeactivatedMessage();

            when(apartmentService.disableApartment(eq(apartmentId), any(User.class))).thenReturn(response);

            mockMvc.perform(delete("/apartment/delete/{apartmentId}", apartmentId)
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", is("Apartamento desativado com sucesso")));
        }

        @Test
        void withApartmentBelongingToAnotherUser_ShouldReturnNotFound() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var apartmentId = UUID.randomUUID();

            when(apartmentService.disableApartment(eq(apartmentId), any(User.class)))
                    .thenThrow(new ApartmentNotFoundException());

            mockMvc.perform(delete("/apartment/delete/{apartmentId}", apartmentId)
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isNotFound());
        }

    }

}
