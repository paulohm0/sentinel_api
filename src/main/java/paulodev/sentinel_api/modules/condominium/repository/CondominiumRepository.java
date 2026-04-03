package paulodev.sentinel_api.modules.condominium.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import paulodev.sentinel_api.modules.condominium.entity.Condominium;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CondominiumRepository extends JpaRepository<Condominium, UUID> {

    @Query("SELECT c FROM Condominium c WHERE c.id = :condominiumId AND c.user.id = :userId")
    Optional<Condominium> findByIdAndUser(@Param("condominiumId") UUID condominiumId, @Param("userId") UUID userId);

    @Query("SELECT c FROM Condominium c WHERE c.user.id = :userId")
    Optional<List<Condominium>> findByUser(@Param("userId") UUID userId);

}
