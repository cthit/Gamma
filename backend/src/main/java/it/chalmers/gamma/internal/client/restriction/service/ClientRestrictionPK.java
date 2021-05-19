package it.chalmers.gamma.internal.client.restriction.service;

import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import it.chalmers.gamma.internal.client.service.ClientId;
import it.chalmers.gamma.util.domain.abstraction.Id;

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
