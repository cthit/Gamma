package it.chalmers.gamma.adapter.secondary.jpa.client.restriction;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntity;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "g_client_restriction_post")
public class ClientRestrictionPostEntity extends ImmutableEntity<ClientRestrictionPostPK> {

    @EmbeddedId
    private ClientRestrictionPostPK id;

    protected ClientRestrictionPostEntity() {
    }

    protected ClientRestrictionPostEntity(ClientEntity clientEntity, SuperGroupEntity superGroupEntity, PostEntity postEntity) {
        this.id = new ClientRestrictionPostPK(superGroupEntity, postEntity, clientEntity);
    }

    public ClientRestrictionPostPK getId() {
        return this.id;
    }

}
