package it.chalmers.gamma.domain.userapproval.data.db;

import it.chalmers.gamma.domain.BaseEntity;
import it.chalmers.gamma.domain.userapproval.data.dto.UserApprovalDTO;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "it_user_approval")
public class UserApproval implements BaseEntity<UserApprovalDTO> {

    @EmbeddedId
    private UserApprovalPK id;

    protected UserApproval() {}

    public UserApproval(UserApprovalDTO userApproval) {
        this.id = new UserApprovalPK(userApproval.getUserId(), userApproval.getClientId());
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

