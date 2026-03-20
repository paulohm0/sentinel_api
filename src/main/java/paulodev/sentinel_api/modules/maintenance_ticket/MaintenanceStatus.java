package paulodev.sentinel_api.modules.maintenance_ticket;

import lombok.Getter;

@Getter
public enum MaintenanceStatus {
    OPEN("Aberto"),
    IN_PROGRESS("Em andamento"),
    CLOSED("Finalizado");

    private final String status;

    MaintenanceStatus(String status) {
        this.status = status;
    }
}
