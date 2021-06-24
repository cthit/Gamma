package it.chalmers.gamma.app.service;

import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.app.domain.ClientId;
import it.chalmers.gamma.util.entity.Id;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class ClientRestrictionPK extends Id<ClientRestrictionPK.ClientRestrictionPKDTO> {

    @Embedded
    private ClientId clientId;

    @Embedded
    private AuthorityLevelName authorityLevelName;

    protected ClientRestrictionPK() { }

    public ClientRestrictionPK(ClientId clientId, AuthorityLevelName authorityLevelName) {
        this.clientId = clientId;
        this.authorityLevelName = authorityLevelName;
    }

    @Override
    protected ClientRestrictionPKDTO get() {
        return new ClientRestrictionPKDTO(
                this.clientId,
                this.authorityLevelName
        );
    }

    protected record ClientRestrictionPKDTO(ClientId clientId,
                                          AuthorityLevelName authorityLevelName) { }


}
