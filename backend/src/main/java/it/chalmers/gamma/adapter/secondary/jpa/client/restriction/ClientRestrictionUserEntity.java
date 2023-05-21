package it.chalmers.gamma.adapter.secondary.jpa.client.restriction;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.app.authority.domain.AuthorityName;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "client_restriction_user")
public class ClientRestrictionUserEntity extends ImmutableEntity<ClientRestrictionUserPK> {

    @EmbeddedId
    private ClientRestrictionUserPK id;

    protected ClientRestrictionUserEntity() {
    }

    protected ClientRestrictionUserEntity(ClientEntity clientEntity, UserEntity userEntity) {
        this.id = new ClientRestrictionUserPK(clientEntity, userEntity);
    }

    public ClientRestrictionUserPK getId() {
        return this.id;
    }

}
