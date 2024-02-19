package it.chalmers.gamma.adapter.secondary.jpa.client.restriction;

import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
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

    public ClientRestrictionSuperGroupEntity(ClientRestrictionEntity clientRestrictionEntity, SuperGroupEntity superGroupEntity) {
        this.id = new ClientRestrictionSuperGroupPK(clientRestrictionEntity, superGroupEntity);
    }

    public ClientRestrictionSuperGroupPK getId() {
        return this.id;
    }

}
