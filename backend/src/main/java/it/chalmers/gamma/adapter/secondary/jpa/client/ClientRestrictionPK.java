package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.client.domain.ClientUid;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Embeddable
public class ClientRestrictionPK extends PKId<ClientRestrictionPK.ClientRestrictionPKDTO> {

    @ManyToOne
    @JoinColumn(name = "client_uid")
    private ClientEntity client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "authority_level")
    private AuthorityLevelEntity authorityLevel;

    protected ClientRestrictionPK() {
    }

    protected ClientRestrictionPK(ClientEntity clientEntity, AuthorityLevelEntity authorityLevelEntity) {
        this.client = clientEntity;
        this.authorityLevel = authorityLevelEntity;
    }

    @Override
    public ClientRestrictionPKDTO getValue() {
        return new ClientRestrictionPKDTO(
                new ClientUid(this.client.getId()),
                AuthorityLevelName.valueOf(this.authorityLevel.getAuthorityLevel())
        );
    }

    protected record ClientRestrictionPKDTO(ClientUid clientUid,
                                            AuthorityLevelName authorityLevelName) {
    }

}
