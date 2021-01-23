package it.chalmers.gamma.approval;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;

@Embeddable
@SuppressFBWarnings(justification = "Fields should be serializable", value = "SE_BAD_FIELD")
public class ITUserApprovalPK implements Serializable {

    @JoinColumn(name = "ituser_id")
    private UUID user;

    @JoinColumn(name = "itclient_id")
    private UUID client;

    protected ITUserApprovalPK() {}

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
