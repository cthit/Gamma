package it.chalmers.gamma.internal.clientrestriction.service;

import it.chalmers.gamma.domain.ClientRestrictions;
import it.chalmers.gamma.util.domain.abstraction.ImmutableEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Collections;

@Entity
@Table(name = "itclient_authority_level_restriction")
public class ClientRestrictionEntity extends ImmutableEntity<ClientRestrictionPK, ClientRestrictions> {

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
    protected ClientRestrictions toDTO() {
        return new ClientRestrictions(
                this.id.get().clientId(),
                Collections.singletonList(this.id.get().authorityLevelName())
        );
    }
}
