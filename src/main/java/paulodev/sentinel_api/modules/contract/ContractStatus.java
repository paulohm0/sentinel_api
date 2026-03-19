package paulodev.sentinel_api.modules.contract;

import lombok.Getter;

@Getter
public enum ContractStatus {
    ACTIVE("ACTIVE"),
    FINISHED("FINISHED"),
    CANCELED("FINISHED");

    private final String status;

    ContractStatus(String status) { this.status = status; }
}
