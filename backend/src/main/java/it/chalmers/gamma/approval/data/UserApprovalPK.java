package it.chalmers.gamma.approval.data;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;

@Embeddable
@SuppressFBWarnings(justification = "Fields should be serializable", value = "SE_BAD_FIELD")
public class UserApprovalPK implements Serializable {

    @JoinColumn(name = "ituser_id")
    private UUID user;

    @JoinColumn(name = "itclient_id")
    private UUID client;

    protected UserApprovalPK() {}

    public UUID getUser() {
        return user;
    }

    public void setUser(UUID user) {
        this.user = user;
    }

    public UUID getClient() {
        return client;
    }

    public void setClient(UUID client) {
        this.client = client;
    }
}
