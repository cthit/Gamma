package it.chalmers.gamma.approval;

import it.chalmers.gamma.client.ITClient;
import it.chalmers.gamma.user.dto.UserDTO;

public class ITUserApprovalPKDTO {

    private final UserDTO user;
    private final ITClient client;

    public ITUserApprovalPKDTO(UserDTO user, ITClient client) {
        this.user = user;
        this.client = client;
    }

    public UserDTO getUser() {
        return user;
    }

    public ITClient getClient() {
        return client;
    }
}
