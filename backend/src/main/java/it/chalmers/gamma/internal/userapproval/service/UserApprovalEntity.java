package it.chalmers.gamma.internal.userapproval.service;

import it.chalmers.gamma.domain.UserApproval;
import it.chalmers.gamma.util.entity.ImmutableEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "it_user_approval")
public class UserApprovalEntity extends ImmutableEntity<UserApprovalPK, UserApproval> {

    @EmbeddedId
    private UserApprovalPK id;

    protected UserApprovalEntity() {}

    public UserApprovalEntity(UserApproval userApproval) {
        this.id = new UserApprovalPK(userApproval.userId(), userApproval.clientId());
    }

    @Override
    protected UserApproval toDTO() {
        return new UserApproval(
                this.id.get().userId(),
                this.id.get().clientId()
        );
    }

    @Override
    protected UserApprovalPK id() {
        return this.id;
    }
}

