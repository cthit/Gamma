package it.chalmers.gamma.internal.userapproval.service;

import it.chalmers.gamma.util.domain.abstraction.ImmutableEntity;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;
import it.chalmers.gamma.util.domain.abstraction.Id;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "it_user_approval")
public class UserApproval extends ImmutableEntity<UserApprovalPK, UserApprovalDTO> {

    @EmbeddedId
    private UserApprovalPK id;

    protected UserApproval() {}

    public UserApproval(UserApprovalDTO userApproval) {
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

