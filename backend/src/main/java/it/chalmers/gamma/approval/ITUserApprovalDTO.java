package it.chalmers.gamma.approval;

import it.chalmers.gamma.client.ITClientDTO;
import it.chalmers.gamma.user.ITUserDTO;

import java.util.Objects;
import java.util.UUID;

public class ITUserApprovalDTO {

    private final UUID clientId;
    private final UUID userId;

    public ITUserApprovalDTO(UUID clientId, UUID userId) {
        this.clientId = clientId;
        this.userId = userId;
    }

    public UUID getClientId() {
        return clientId;
    }

    public UUID getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ITUserApprovalDTO that = (ITUserApprovalDTO) o;
        return Objects.equals(clientId, that.clientId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, userId);
    }

    @Override
    public String toString() {
        return "ITUserApprovalDTO{" +
                "clientId=" + clientId +
                ", userId=" + userId +
                '}';
    }
}
