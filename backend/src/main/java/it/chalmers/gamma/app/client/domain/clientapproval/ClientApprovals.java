package it.chalmers.gamma.app.client.domain.clientapproval;

import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.user.domain.GammaUser;

import java.util.List;
import java.util.Objects;

public record ClientApprovals(Client client, List<GammaUser> approvals) {

    public ClientApprovals {
        Objects.requireNonNull(client);
        Objects.requireNonNull(approvals);
    }

}
