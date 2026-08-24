package paulodev.sentinel_api.modules.apartment.entity;

import jakarta.persistence.*;
import lombok.*;
import paulodev.sentinel_api.modules.condominium.entity.Condominium;
import paulodev.sentinel_api.modules.contract.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_apartments")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Apartment {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "number", nullable = false)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condominium_id", nullable = false)
    private Condominium condominium;

    @OneToMany(mappedBy = "apartment")
    private List<Contract> contracts;

    @Enumerated(EnumType.STRING)
    @Column(name = "apartment_status", nullable = false)
    private ApartmentStatus apartmentStatus;

    public Apartment(String number, Condominium condominium) {
        this.number = number;
        this.condominium = condominium;
        this.contracts = new ArrayList<>();
        this.apartmentStatus = ApartmentStatus.ACTIVE;
    }
}
