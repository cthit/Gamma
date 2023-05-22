package it.chalmers.gamma.adapter.secondary.jpa.client.authority;

import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "g_client_authority_post")
public class ClientAuthorityPostEntity extends ImmutableEntity<ClientAuthorityPostPK> {

    @EmbeddedId
    protected ClientAuthorityPostPK id;

    protected ClientAuthorityPostEntity() {
    }

    public ClientAuthorityPostEntity(SuperGroupEntity superGroup, PostEntity postEntity, ClientAuthorityEntity clientAuthorityEntity) {
        this.id = new ClientAuthorityPostPK(superGroup, postEntity, clientAuthorityEntity);
    }

    @Override
    public ClientAuthorityPostPK getId() {
        return this.id;
    }

}

