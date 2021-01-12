package it.chalmers.gamma.approval;

import it.chalmers.gamma.client.ITClient;
import it.chalmers.gamma.user.ITUserDTO;

public class ITUserApprovalPKDTO {

    private final ITUserDTO user;
    private final ITClient client;

    public ITUserApprovalPKDTO(ITUserDTO user, ITClient client) {
        this.user = user;
        this.client = client;
    }

    public ITUserDTO getUser() {
        return user;
    }

    public ITClient getClient() {
        return client;
    }
}
