package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "it_user_approval")
public class UserApprovalEntity extends ImmutableEntity<UserApprovalEntityPK> {

    @EmbeddedId
    private UserApprovalEntityPK id;

    protected UserApprovalEntity() {}

    @Override
    protected UserApprovalEntityPK id() {
        return this.id;
    }
}

