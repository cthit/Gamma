package it.chalmers.gamma.app.usecase;

import it.chalmers.gamma.app.domain.user.Cid;
import it.chalmers.gamma.app.domain.client.ClientId;
import org.springframework.stereotype.Service;

@Service
public class ClientUserApprovalUseCase {

    public void approveUserForClient(Cid cid, ClientId clientId) {
        throw new UnsupportedOperationException();
    }

    public void revokeApprovalOfUserForClient(Cid cid, ClientId clientId) {
        throw new UnsupportedOperationException();
    }

    public boolean userHaveApprovedClient(Cid cid, ClientId clientId) {
        throw new UnsupportedOperationException();
    }

}
