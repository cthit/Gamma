package it.chalmers.gamma.domain.userapproval.data.dto;

import it.chalmers.gamma.domain.DTO;
import it.chalmers.gamma.domain.client.domain.ClientId;
import it.chalmers.gamma.domain.user.UserId;

public class UserApprovalDTO implements DTO {

    private final UserId userId;
    private final ClientId clientId;

    public UserApprovalDTO(UserId userId, ClientId clientId) {
        this.userId = userId;
        this.clientId = clientId;
    }

    public ClientId getClientId() {
        return clientId;
    }

    public UserId getUserId() {
        return userId;
    }

}
