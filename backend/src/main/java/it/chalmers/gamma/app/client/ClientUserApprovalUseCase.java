package it.chalmers.gamma.app.client;

import it.chalmers.gamma.domain.user.Cid;
import it.chalmers.gamma.domain.client.ClientId;

public interface ClientUserApprovalUseCase {

    void approveUserForClient(Cid cid, ClientId clientId);
    void revokeApprovalOfUserForClient(Cid cid, ClientId clientId);
    boolean userHaveApprovedClient(Cid cid, ClientId clientId);

}
