package it.chalmers.gamma.db.entity.pk;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import it.chalmers.gamma.db.entity.ITClient;
import it.chalmers.gamma.db.entity.ITUser;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
@SuppressFBWarnings(justification = "Fields should be serializable", value = "SE_BAD_FIELD")
public class ITUserApprovalPK implements Serializable {

    @ManyToOne
    @JoinColumn(name = "ituser_id")
    private ITUser itUser;

    @ManyToOne
    @JoinColumn(name = "itclient_id")
    private ITClient itClient;
    
    public ITUser getItUser() {
        return this.itUser;
    }

    public void setItUser(ITUser itUser) {
        this.itUser = itUser;
    }

    public ITClient getItClient() {
        return this.itClient;
    }

    public void setItClient(ITClient itClient) {
        this.itClient = itClient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ITUserApprovalPK that = (ITUserApprovalPK) o;
        return Objects.equals(this.itUser, that.itUser)
            && Objects.equals(this.itClient, that.itClient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.itUser, this.itClient);
    }

    @Override
    public String toString() {
        return "ITUserApprovalPK{"
            + "itUser=" + this.itUser
            + ", itClient=" + this.itClient
            + '}';
    }
}
