package it.chalmers.gamma.adapter.secondary.jpa.client.restriction;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.app.authority.domain.AuthorityName;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "g_client_restriction_super_group")
public class ClientRestrictionSuperGroupEntity extends ImmutableEntity<ClientRestrictionSuperGroupPK> {

    @EmbeddedId
    private ClientRestrictionSuperGroupPK id;

    protected ClientRestrictionSuperGroupEntity() {
    }

    protected ClientRestrictionSuperGroupEntity(ClientEntity clientEntity, SuperGroupEntity superGroupEntity) {
        this.id = new ClientRestrictionSuperGroupPK(clientEntity, superGroupEntity);
    }

    public ClientRestrictionSuperGroupPK getId() {
        return this.id;
    }

}
