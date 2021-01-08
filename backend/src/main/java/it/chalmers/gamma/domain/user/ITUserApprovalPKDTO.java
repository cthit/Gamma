package it.chalmers.gamma.domain.user;

import it.chalmers.gamma.client.ITClient;

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
