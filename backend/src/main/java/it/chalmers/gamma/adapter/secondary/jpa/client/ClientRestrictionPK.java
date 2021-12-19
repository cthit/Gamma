package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.client.domain.ClientUid;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class ClientRestrictionPK extends PKId<ClientRestrictionPK.ClientRestrictionPKDTO> {

    @ManyToOne
    @JoinColumn(name = "client_uid")
    private ClientEntity client;

    @ManyToOne
    @JoinColumn(name = "authority_level")
    private AuthorityLevelEntity authorityLevel;

    protected ClientRestrictionPK() {}

    protected ClientRestrictionPK(ClientEntity clientEntity, AuthorityLevelEntity authorityLevelEntity) {
        this.client = clientEntity;
        this.authorityLevel = authorityLevelEntity;
    }

    protected record ClientRestrictionPKDTO(ClientUid clientUid,
                                            AuthorityLevelName authorityLevelName) { }

    @Override
    public ClientRestrictionPKDTO getValue() {
        return new ClientRestrictionPKDTO(
                this.client.domainId(),
                AuthorityLevelName.valueOf(this.authorityLevel.getAuthorityLevel())
        );
    }

}
