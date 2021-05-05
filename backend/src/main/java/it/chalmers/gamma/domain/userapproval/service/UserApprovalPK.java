package it.chalmers.gamma.domain.userapproval.service;

import it.chalmers.gamma.util.domain.abstraction.Id;
import it.chalmers.gamma.domain.client.service.ClientId;
import it.chalmers.gamma.domain.user.service.UserId;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class UserApprovalPK implements Id, Serializable {

    @Embedded
    private UserId userId;

    @Embedded
    private ClientId clientId;

    protected UserApprovalPK() {}

    public UserApprovalPK(UserId userId, ClientId clientId) {
        this.userId = userId;
        this.clientId = clientId;
    }

    public UserId getUserId() {
        return userId;
    }

    public ClientId getClientId() {
        return clientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserApprovalPK that = (UserApprovalPK) o;
        return Objects.equals(userId, that.userId) && Objects.equals(clientId, that.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, clientId);
    }
}
