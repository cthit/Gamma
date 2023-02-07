package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "itclient_authority_level_restriction")
public class ClientRestrictionEntity extends ImmutableEntity<ClientRestrictionPK> {

    @EmbeddedId
    private ClientRestrictionPK id;

    protected ClientRestrictionEntity() {
    }

    protected ClientRestrictionEntity(ClientEntity clientEntity, AuthorityLevelName authorityLevelName) {
        this.id = new ClientRestrictionPK(clientEntity, authorityLevelName);
    }

    public ClientRestrictionPK getId() {
        return this.id;
    }

    public AuthorityLevelName getAuthorityLevelName() {
        return this.id.getValue().authorityLevelName();
    }
}
