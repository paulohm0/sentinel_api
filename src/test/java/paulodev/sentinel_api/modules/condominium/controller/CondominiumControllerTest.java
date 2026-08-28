package paulodev.sentinel_api.modules.condominium.controller;

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
import paulodev.sentinel_api.exception.custom.condominium.CondominiumEmptyListException;
import paulodev.sentinel_api.exception.custom.condominium.CondominiumNotFoundException;
import paulodev.sentinel_api.modules.apartment.dto.ApartmentResponse;
import paulodev.sentinel_api.modules.condominium.dto.CondominiumDeactivatedMessage;
import paulodev.sentinel_api.modules.condominium.dto.CondominiumDetailsResponse;
import paulodev.sentinel_api.modules.condominium.dto.CondominiumRegisterRequest;
import paulodev.sentinel_api.modules.condominium.dto.CondominiumResponse;
import paulodev.sentinel_api.modules.condominium.dto.CondominiumUpdateRequest;
import paulodev.sentinel_api.modules.condominium.entity.Condominium;
import paulodev.sentinel_api.modules.condominium.service.CondominiumService;
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

@WebMvcTest(value = CondominiumController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class}
)
class CondominiumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CondominiumService condominiumService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    @Nested
    class Register {

        @Test
        void withValidRequest() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var request = new CondominiumRegisterRequest("Cond Alpha", "Rua A, 100");
            var condominium = new Condominium("Cond Alpha", "Rua A, 100", authenticatedUser);
            var response = new CondominiumResponse(condominium);

            when(condominiumService.register(request, authenticatedUser)).thenReturn(response);

            mockMvc.perform(post("/condominium/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name", is("Cond Alpha")));
        }

        @Test
        void withBlankName() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var request = new CondominiumRegisterRequest("", "Rua A, 100");

            mockMvc.perform(post("/condominium/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void withBlankAddress() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var request = new CondominiumRegisterRequest("Cond Alpha", "");

            mockMvc.perform(post("/condominium/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isBadRequest());
        }

    }

    @Nested
    class GetCondominiumListByUser {

        @Test
        void withNonEmptyList() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var condo1 = new Condominium("Cond Alpha", "Rua A, 100", authenticatedUser);
            var condo2 = new Condominium("Cond Beta", "Rua B, 200", authenticatedUser);
            var response = List.of(new CondominiumResponse(condo1), new CondominiumResponse(condo2));

            when(condominiumService.getCondominiumListByUser(authenticatedUser)).thenReturn(response);

            mockMvc.perform(get("/condominium/list")
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()", is(2)))
                    .andExpect(jsonPath("$[0].name", is("Cond Alpha")));
        }

        @Test
        void withEmptyList_ShouldReturnNotFound() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);

            when(condominiumService.getCondominiumListByUser(authenticatedUser))
                    .thenThrow(new CondominiumEmptyListException());

            mockMvc.perform(get("/condominium/list")
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isNotFound());
        }

    }

    @Nested
    class GetCondominiumSummary {

        @Test
        void withOwnedCondominium() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var condominiumId = UUID.randomUUID();
            var condominium = new Condominium("Cond Alpha", "Rua A, 100", authenticatedUser);
            var response = new CondominiumResponse(condominium);

            when(condominiumService.getCondominiumInfo(eq(condominiumId), any(User.class))).thenReturn(response);

            mockMvc.perform(get("/condominium/summary/{condominiumId}", condominiumId)
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("Cond Alpha")));
        }

        @Test
        void withCondominiumBelongingToAnotherUser_ShouldReturnNotFound() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var condominiumId = UUID.randomUUID();

            when(condominiumService.getCondominiumInfo(eq(condominiumId), any(User.class)))
                    .thenThrow(new CondominiumNotFoundException());

            mockMvc.perform(get("/condominium/summary/{condominiumId}", condominiumId)
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isNotFound());
        }

    }

    @Nested
    class GetCondominiumDetails {

        @Test
        void withOwnedCondominium() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var condominiumId = UUID.randomUUID();
            var response = new CondominiumDetailsResponse(
                    condominiumId,
                    "Cond Alpha",
                    "Rua A, 100",
                    1,
                    List.of(new ApartmentResponse(UUID.randomUUID(), "101", null)),
                    null);

            when(condominiumService.listCondominiumApartments(eq(condominiumId), any(User.class))).thenReturn(response);

            mockMvc.perform(get("/condominium/details/{condominiumId}", condominiumId)
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("Cond Alpha")))
                    .andExpect(jsonPath("$.totalApartments", is(1)))
                    .andExpect(jsonPath("$.apartments[0].number", is("101")));
        }

        @Test
        void withCondominiumBelongingToAnotherUser_ShouldReturnNotFound() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var condominiumId = UUID.randomUUID();

            when(condominiumService.listCondominiumApartments(eq(condominiumId), any(User.class)))
                    .thenThrow(new CondominiumNotFoundException());

            mockMvc.perform(get("/condominium/details/{condominiumId}", condominiumId)
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isNotFound());
        }

    }

    @Nested
    class UpdateCondominium {

        @Test
        void withValidRequest() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var condominiumId = UUID.randomUUID();
            var request = new CondominiumUpdateRequest("Cond Alpha Renovado", "Rua Nova, 200");
            var condominium = new Condominium("Cond Alpha Renovado", "Rua Nova, 200", authenticatedUser);
            var response = new CondominiumResponse(condominium);

            when(condominiumService.updateCondominium(eq(condominiumId), eq(request), any(User.class)))
                    .thenReturn(response);

            mockMvc.perform(patch("/condominium/update/{condominiumId}", condominiumId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("Cond Alpha Renovado")));
        }

        @Test
        void withBlankName() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var condominiumId = UUID.randomUUID();
            var request = new CondominiumUpdateRequest("", "Rua Nova, 200");

            mockMvc.perform(patch("/condominium/update/{condominiumId}", condominiumId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void withCondominiumBelongingToAnotherUser_ShouldReturnNotFound() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var condominiumId = UUID.randomUUID();
            var request = new CondominiumUpdateRequest("Novo Nome", "Novo Endereço");

            when(condominiumService.updateCondominium(eq(condominiumId), eq(request), any(User.class)))
                    .thenThrow(new CondominiumNotFoundException());

            mockMvc.perform(patch("/condominium/update/{condominiumId}", condominiumId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isNotFound());
        }

    }

    @Nested
    class DisableCondominium {

        @Test
        void withOwnedCondominium() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var condominiumId = UUID.randomUUID();
            var response = new CondominiumDeactivatedMessage();

            when(condominiumService.disableCondominium(eq(condominiumId), any(User.class))).thenReturn(response);

            mockMvc.perform(delete("/condominium/delete/{condominiumId}", condominiumId)
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", is("Condomínio desativado com sucesso")));
        }

        @Test
        void withCondominiumBelongingToAnotherUser_ShouldReturnNotFound() throws Exception {
            User authenticatedUser = new User("Paulo", "paulo@test.com", "pass", UserRole.USER);
            var condominiumId = UUID.randomUUID();

            when(condominiumService.disableCondominium(eq(condominiumId), any(User.class)))
                    .thenThrow(new CondominiumNotFoundException());

            mockMvc.perform(delete("/condominium/delete/{condominiumId}", condominiumId)
                    .with(SecurityMockMvcRequestPostProcessors.user(authenticatedUser)))
                    .andExpect(status().isNotFound());
        }

    }

}
