package paulodev.sentinel_api.modules.contract;

import lombok.Getter;

@Getter
public enum ContractStatus {
    ACTIVE("Ativo"),
    FINISHED("Finalizado"),
    CANCELED("Cancelado");

    private final String status;

    ContractStatus(String status) { this.status = status; }
}
