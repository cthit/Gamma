package it.chalmers.gamma.app.service;

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
    protected ClientRestriction toDTO() {
        return new ClientRestriction(
                this.id.get().clientId(),
                this.id.get().authorityLevelName()
        );
    }
}
