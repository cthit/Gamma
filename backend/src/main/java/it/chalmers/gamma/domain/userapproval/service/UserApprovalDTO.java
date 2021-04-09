package it.chalmers.gamma.domain.userapproval.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.domain.client.service.ClientId;
import it.chalmers.gamma.domain.user.service.UserId;

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
