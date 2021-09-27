package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.app.domain.client.ClientId;
import it.chalmers.gamma.app.domain.Id;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ClientRestrictionPK implements Id<ClientRestrictionPK.ClientRestrictionPKDTO> {

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "authority_level")
    private String authorityLevelName;

    protected ClientRestrictionPK() { }

    public ClientRestrictionPK(ClientId clientId, AuthorityLevelName authorityLevelName) {
        this.clientId = clientId.getValue();
        this.authorityLevelName = authorityLevelName.getValue();
    }

    @Override
    public ClientRestrictionPKDTO getValue() {
        return new ClientRestrictionPKDTO(
                ClientId.valueOf(this.clientId),
                AuthorityLevelName.valueOf(this.authorityLevelName)
        );
    }

    protected record ClientRestrictionPKDTO(ClientId clientId,
                                          AuthorityLevelName authorityLevelName) { }

}
