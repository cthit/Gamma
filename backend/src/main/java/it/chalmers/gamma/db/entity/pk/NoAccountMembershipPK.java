package it.chalmers.gamma.db.entity.pk;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.ITUser;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@SuppressFBWarnings(justification = "Fields should be serializable", value = "SE_BAD_FIELD")
public class NoAccountMembershipPK implements Serializable {

    private static final long serialVersionUID = 6624119509779427L;
    @JoinColumn(name = "ituser")
    private String itUser;
    @ManyToOne
    @JoinColumn(name = "fkit_group_id")
    private FKITGroup fkitGroup;

    public String getITUser() {
        return this.itUser;
    }

    public void setITUser(String ituserId) {
        this.itUser = ituserId;
    }

    public FKITGroup getFKITGroup() {
        return this.fkitGroup;
    }

    public void setFKITGroup(FKITGroup fkitGroupId) {
        this.fkitGroup = fkitGroupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoAccountMembershipPK that = (NoAccountMembershipPK) o;
        return Objects.equals(itUser, that.itUser) &&
                Objects.equals(fkitGroup, that.fkitGroup);
    }

    @Override
    public int hashCode() {

        return Objects.hash(itUser, fkitGroup);
    }

    @Override
    public String toString() {
        return "NoAccountMembershipPK{" +
                "itUser='" + itUser + '\'' +
                ", fkitGroup=" + fkitGroup +
                '}';
    }
}
