package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevelName;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "itclient_authority_level_restriction")
public class ClientRestrictionEntity extends ImmutableEntity<ClientRestrictionPK> {

    @EmbeddedId
    private ClientRestrictionPK id;

    protected ClientRestrictionEntity() { }

    protected ClientRestrictionEntity(ClientEntity clientEntity, AuthorityLevelEntity authorityLevelEntity) {
        this.id = new ClientRestrictionPK(clientEntity, authorityLevelEntity);
    }

    protected ClientRestrictionPK domainId() {
        return this.id;
    }

    public AuthorityLevelName getAuthorityLevelName() {
        return this.id.getValue().authorityLevelName();
    }
}
