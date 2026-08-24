package paulodev.sentinel_api.modules.apartment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import paulodev.sentinel_api.modules.apartment.entity.Apartment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, UUID> {

    Optional<Apartment> findByNumberAndCondominiumId(String number, UUID condominiumId);

    List<Apartment> findByCondominiumId(UUID condominiumId);

    @Query("SELECT a FROM Apartment a WHERE a.id = :apartmentId AND a.condominium.user.id = :userId")
    Optional<Apartment> findByIdAndUser(@Param("apartmentId") UUID apartmentId, @Param("userId") UUID userId);
}
