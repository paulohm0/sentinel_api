package paulodev.sentinel_api.modules.condominium.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import paulodev.sentinel_api.exception.custom.condominium.CondominiumEmptyListException;
import paulodev.sentinel_api.exception.custom.condominium.CondominiumNotFoundException;
import paulodev.sentinel_api.modules.condominium.dto.CondominiumRegisterRequest;
import paulodev.sentinel_api.modules.condominium.dto.CondominiumResponse;
import paulodev.sentinel_api.modules.condominium.dto.CondominiumDetailsResponse;
import paulodev.sentinel_api.modules.condominium.entity.Condominium;
import paulodev.sentinel_api.modules.condominium.repository.CondominiumRepository;
import paulodev.sentinel_api.modules.user.entity.User;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CondominiumService {

    private final CondominiumRepository condominiumRepository;

    @Transactional
    public CondominiumResponse register(CondominiumRegisterRequest request, User user) {
        Condominium newCondominium = new Condominium(
                request.name(),
                request.address(),
                user);
        Condominium savedCondominium = condominiumRepository.save(newCondominium);
        return new CondominiumResponse(savedCondominium);
    }

    @Transactional
    public CondominiumResponse getCondominiumInfo(UUID condominiumId, User authenticatedUser) {
        Condominium condominium = condominiumRepository.findByIdAndUser(condominiumId, authenticatedUser.getId())
                .orElseThrow(CondominiumNotFoundException::new);
        return new CondominiumResponse(condominium);
    }

    @Transactional
    public List<CondominiumResponse> getCondominiumListByUser(User authenticatedUser) {
        return condominiumRepository.findByUser(authenticatedUser.getId())
                .filter(condominiumList -> !condominiumList.isEmpty())
                .map(condominiumList -> condominiumList
                        .stream()
                        .map(CondominiumResponse::new)
                        .toList())
                .orElseThrow(CondominiumEmptyListException::new);
    }

    @Transactional
    public CondominiumDetailsResponse listCondominiumApartments(UUID condominiumId, User authenticatedUser) {
        Condominium condominium = condominiumRepository.findByIdAndUser(condominiumId, authenticatedUser.getId())
                .orElseThrow(CondominiumNotFoundException::new);
        return new CondominiumDetailsResponse(condominium);
    }
}


