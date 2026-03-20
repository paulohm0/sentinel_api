package paulodev.sentinel_api.modules.condominium;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import paulodev.sentinel_api.modules.apartment.Apartment;
import paulodev.sentinel_api.modules.user.entity.User;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_condominiums")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Condominium {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "condominium")
    @JsonIgnore
    private List<Apartment> apartments;
}

