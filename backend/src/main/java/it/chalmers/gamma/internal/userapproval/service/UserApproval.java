package it.chalmers.gamma.internal.userapproval.service;

import it.chalmers.gamma.util.domain.abstraction.BaseEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "it_user_approval")
public class UserApproval extends BaseEntity<UserApprovalDTO> {

    @EmbeddedId
    private UserApprovalPK id;

    protected UserApproval() {}

    public UserApproval(UserApprovalDTO userApproval) {
        this.id = new UserApprovalPK(userApproval.userId(), userApproval.clientId());
    }

    public UserApprovalPK getId() {
        return this.id;
    }

    @Override
    public UserApprovalDTO toDTO() {
        return new UserApprovalDTO(
                this.id.getUserId(),
                this.id.getClientId()
        );
    }
}

