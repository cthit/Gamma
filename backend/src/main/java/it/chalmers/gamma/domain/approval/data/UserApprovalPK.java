package it.chalmers.gamma.domain.approval.data;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.chalmers.gamma.domain.user.UserId;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
@SuppressFBWarnings(justification = "Fields should be serializable", value = "SE_BAD_FIELD")
public class UserApprovalPK implements Serializable {

    @Embedded
    private UserId userId;

    @Column(name = "itclient_id")
    private String clientId;

    protected UserApprovalPK() {}

    public UserApprovalPK(UserId userId, String clientId) {
        this.userId = userId;
        this.clientId = clientId;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getClientId() {
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
