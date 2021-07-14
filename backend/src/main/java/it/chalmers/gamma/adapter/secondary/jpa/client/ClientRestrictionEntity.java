package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.app.domain.ClientRestriction;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "itclient_authority_level_restriction")
public class ClientRestrictionEntity extends ImmutableEntity<ClientRestrictionPK, ClientRestriction> {

    @EmbeddedId
    private ClientRestrictionPK id;

    protected ClientRestrictionEntity() { }

    protected ClientRestrictionEntity(ClientRestrictionPK id) {
        this.id = id;
    }

    @Override
    protected ClientRestrictionPK id() {
        return this.id;
    }

    @Override
    protected ClientRestriction toDomain() {
        return new ClientRestriction(
                this.id.value().clientId(),
                this.id.value().authorityLevelName()
        );
    }
}
