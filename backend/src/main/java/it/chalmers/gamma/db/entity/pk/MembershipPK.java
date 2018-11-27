package it.chalmers.gamma.db.entity.pk;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.ITUser;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class MembershipPK implements Serializable {

    @ManyToOne
    @JoinColumn(name = "ituser_id")
    private ITUser itUser;

    @ManyToOne
    @JoinColumn(name = "fkit_group_id")
    private FKITGroup fkitGroup;

    public ITUser getITUser() {
        return this.itUser;
    }

    public void setITUser(ITUser ituserId) {
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MembershipPK that = (MembershipPK) o;
        return Objects.equals(this.itUser, that.itUser)
                && Objects.equals(this.fkitGroup, that.fkitGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.itUser, this.fkitGroup);
    }

    @Override
    public String toString() {
        return "MembershipPK{"
                + "itUser=" + this.itUser
                + ", fkitGroup=" + this.fkitGroup
                + '}';
    }
}
