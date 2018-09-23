package it.chalmers.gamma.db.entity.pk;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.ITUser;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MembershipPK implements Serializable {

    @ManyToOne
    @JoinColumn(name = "ituser_id")
    private ITUser itUser;

    @ManyToOne
    @JoinColumn(name = "fkit_group_id")
    private FKITGroup fkitGroup;

    public ITUser getITUser() {
        return itUser;
    }

    public void setITUser(ITUser ituserId) {
        this.itUser = ituserId;
    }

    public FKITGroup getFKITGroup() {
        return fkitGroup;
    }

    public void setFKITGroup(FKITGroup fkitGroupId) {
        this.fkitGroup = fkitGroupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MembershipPK that = (MembershipPK) o;
        return Objects.equals(itUser, that.itUser) &&
                Objects.equals(fkitGroup, that.fkitGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itUser, fkitGroup);
    }

    @Override
    public String toString() {
        return "MembershipPK{" +
                "itUser=" + itUser +
                ", fkitGroup=" + fkitGroup +
                '}';
    }
}
