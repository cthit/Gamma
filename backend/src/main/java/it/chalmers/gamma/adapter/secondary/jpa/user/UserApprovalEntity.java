package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "it_user_approval")
public class UserApprovalEntity extends ImmutableEntity<UserApprovalEntityPK> {

    @EmbeddedId
    private UserApprovalEntityPK id;

    public UserApprovalEntity() {}

    public UserApprovalEntity(UserEntity user, ClientEntity client) {
        this.id = new UserApprovalEntityPK(user, client);
    }

    @Override
    protected UserApprovalEntityPK id() {
        return this.id;
    }

    public UserEntity getUserEntity() {
        return this.id.getUserEntity();
    }
}

