package it.chalmers.gamma.internal.user.approval.service;

import it.chalmers.gamma.util.domain.abstraction.Id;
import it.chalmers.gamma.domain.ClientId;
import it.chalmers.gamma.domain.UserId;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class UserApprovalPK extends Id<UserApprovalPK.UserApprovalPKDTO> {

    protected record UserApprovalPKDTO(UserId userId, ClientId clientId) {
    }

    @Embedded
    private UserId userId;

    @Embedded
    private ClientId clientId;

    protected UserApprovalPK() {}

    public UserApprovalPK(UserId userId, ClientId clientId) {
        this.userId = userId;
        this.clientId = clientId;
    }

    @Override
    protected UserApprovalPKDTO get() {
        return new UserApprovalPKDTO(
                this.userId,
                this.clientId
        );
    }
}
