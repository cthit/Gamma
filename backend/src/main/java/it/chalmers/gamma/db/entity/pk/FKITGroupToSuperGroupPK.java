package it.chalmers.gamma.db.entity.pk;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.FKITSuperGroup;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FKITGroupToSuperGroupPK implements Serializable {
    @ManyToOne
    @JoinColumn(name = "fkit_super_group_id")
    FKITSuperGroup superGroup;

    @ManyToOne
    @JoinColumn(name = "fkit_group_id")
    FKITGroup group;

    public FKITGroupToSuperGroupPK(FKITSuperGroup superGroup, FKITGroup group) {
        this.superGroup = superGroup;
        this.group = group;
    }
    public FKITGroupToSuperGroupPK() {

    }
    public FKITSuperGroup getSuperGroup() {
        return superGroup;
    }

    public void setSuperGroup(FKITSuperGroup superGroup) {
        this.superGroup = superGroup;
    }

    public FKITGroup getGroup() {
        return group;
    }

    public void setGroup(FKITGroup group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FKITGroupToSuperGroupPK that = (FKITGroupToSuperGroupPK) o;
        return Objects.equals(superGroup, that.superGroup) &&
                Objects.equals(group, that.group);
    }

    @Override
    public int hashCode() {

        return Objects.hash(superGroup, group);
    }

    @Override
    public String toString() {
        return "FKITGroupToSuperGroupPK{" +
                "superGroup=" + superGroup +
                ", group=" + group +
                '}';
    }
}
