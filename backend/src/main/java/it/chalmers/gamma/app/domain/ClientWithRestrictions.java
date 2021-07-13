package it.chalmers.gamma.app.domain;

import java.util.List;
import java.util.Objects;

public record ClientWithRestrictions(Client client, List<AuthorityLevelName> restrictions) {

    public ClientWithRestrictions {
        Objects.requireNonNull(client);
        Objects.requireNonNull(restrictions);
    }

}
