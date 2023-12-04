package it.chalmers.gamma.app.client.domain.approval;

import it.chalmers.gamma.app.client.domain.ClientId;
import it.chalmers.gamma.app.client.domain.ClientUid;

import java.util.Optional;

public interface ClientApprovalsRepository {

    Optional<ClientApprovals> getByClientId(ClientId clientId);
    Optional<ClientApprovals> getByClientUid(ClientUid clientUid);

}
