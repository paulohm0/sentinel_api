package paulodev.sentinel_api.modules.billing;

import lombok.Getter;

@Getter
public enum BillingStatus {
    PENDING("Pendente"),
    PAID("Pago");

    private final String status;

    BillingStatus(String status) { this.status = status; }
}
