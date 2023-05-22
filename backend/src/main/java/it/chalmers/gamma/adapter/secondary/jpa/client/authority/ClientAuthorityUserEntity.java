package it.chalmers.gamma.adapter.secondary.jpa.client.authority;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "g_client_authority_user")
public class ClientAuthorityUserEntity extends ImmutableEntity<ClientAuthorityUserPK> {

    @EmbeddedId
    private ClientAuthorityUserPK id;

    protected ClientAuthorityUserEntity() {

    }

    public ClientAuthorityUserEntity(UserEntity user, ClientAuthorityEntity clientAuthorityEntity) {
        this.id = new ClientAuthorityUserPK(user, clientAuthorityEntity);
    }

    @Override
    public ClientAuthorityUserPK getId() {
        return this.id;
    }

    public UserEntity getUserEntity() {
        return this.id.getUserEntity();
    }

}
