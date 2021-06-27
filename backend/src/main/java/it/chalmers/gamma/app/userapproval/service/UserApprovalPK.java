package it.chalmers.gamma.app.userapproval.service;

import it.chalmers.gamma.adapter.secondary.jpa.util.Id;
import it.chalmers.gamma.app.domain.ClientId;
import it.chalmers.gamma.app.domain.UserId;

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
