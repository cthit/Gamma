package it.chalmers.gamma.app.client.domain.approval;

import it.chalmers.gamma.app.client.domain.ClientId;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.user.domain.GammaUser;

import java.util.List;

public interface ClientApprovalsRepository {

    List<GammaUser> getAllByClientId(ClientId clientId);
    List<GammaUser> getAllByClientUid(ClientUid clientUid);

}
