package it.chalmers.gamma.domain.approval.data;

import it.chalmers.gamma.domain.user.UserId;

import java.util.Objects;
import java.util.UUID;

public class UserApprovalDTO {

    private final UserId userId;
    private final String clientId;

    public UserApprovalDTO(UserId userId, String clientId) {
        this.userId = userId;
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public UserId getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserApprovalDTO that = (UserApprovalDTO) o;
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
