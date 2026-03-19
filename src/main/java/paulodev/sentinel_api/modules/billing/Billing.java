package paulodev.sentinel_api.modules.billing;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_billings")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Billing {

    private UUID id;

    private String billingMonth;

    private BigDecimal totalAmount;


}
