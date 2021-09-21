package it.chalmers.gamma.app.client;

import it.chalmers.gamma.domain.user.Cid;
import it.chalmers.gamma.domain.client.ClientId;
import org.springframework.stereotype.Service;

@Service
public class ClientUserApprovalUseCase {

    public void approveUserForClient(Cid cid, ClientId clientId) {

    }

    public void revokeApprovalOfUserForClient(Cid cid, ClientId clientId) {

    }

    public boolean userHaveApprovedClient(Cid cid, ClientId clientId) {
        return false;
    }

}
