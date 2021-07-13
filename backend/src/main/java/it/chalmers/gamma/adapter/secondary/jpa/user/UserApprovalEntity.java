package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.domain.UserApproval;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "it_user_approval")
public class UserApprovalEntity extends ImmutableEntity<UserApprovalEntityPK, UserApproval> {

    @EmbeddedId
    private UserApprovalEntityPK id;

    protected UserApprovalEntity() {}

    public UserApprovalEntity(UserApproval userApproval) {
        this.id = new UserApprovalEntityPK(userApproval.user(), userApproval.client());
    }

    @Override
    protected UserApproval toDTO() {
        return new UserApproval(
                this.id.value().user(),
                this.id.value().client()
        );
    }

    @Override
    protected UserApprovalEntityPK id() {
        return this.id;
    }
}

