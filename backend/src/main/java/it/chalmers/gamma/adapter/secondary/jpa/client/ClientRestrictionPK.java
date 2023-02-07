package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PKId;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.client.domain.ClientUid;
import jakarta.persistence.*;

@Embeddable
public class ClientRestrictionPK extends PKId<ClientRestrictionPK.ClientRestrictionPKDTO> {

    @ManyToOne
    @JoinColumn(name = "client_uid")
    private ClientEntity client;

    @Column(name = "authority_level")
    private String authorityLevel;

    protected ClientRestrictionPK() {
    }

    protected ClientRestrictionPK(ClientEntity clientEntity, AuthorityLevelName authorityLevelName) {
        this.client = clientEntity;
        this.authorityLevel = authorityLevelName.value();
    }

    @Override
    public ClientRestrictionPKDTO getValue() {
        return new ClientRestrictionPKDTO(
                new ClientUid(this.client.getId()),
                new AuthorityLevelName(this.authorityLevel)
        );
    }

    protected record ClientRestrictionPKDTO(ClientUid clientUid,
                                            AuthorityLevelName authorityLevelName) {
    }

}
