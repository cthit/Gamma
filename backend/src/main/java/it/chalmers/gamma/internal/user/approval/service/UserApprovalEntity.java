package it.chalmers.gamma.internal.user.approval.service;

import it.chalmers.gamma.util.domain.abstraction.ImmutableEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "it_user_approval")
public class UserApprovalEntity extends ImmutableEntity<UserApprovalPK, UserApprovalDTO> {

    @EmbeddedId
    private UserApprovalPK id;

    protected UserApprovalEntity() {}

    public UserApprovalEntity(UserApprovalDTO userApproval) {
        this.id = new UserApprovalPK(userApproval.userId(), userApproval.clientId());
    }

    @Override
    protected UserApprovalDTO toDTO() {
        return new UserApprovalDTO(
                this.id.get().userId(),
                this.id.get().clientId()
        );
    }

    @Override
    protected UserApprovalPK id() {
        return this.id;
    }
}

