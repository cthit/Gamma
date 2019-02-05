package it.chalmers.gamma.db.entity.pk;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.FKITSuperGroup;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Embeddable
@SuppressFBWarnings(justification = "Fields should be serializable", value = "SE_BAD_FIELD")
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
        //Used by hibernate
    }

    public FKITSuperGroup getSuperGroup() {
        return this.superGroup;
    }

    public void setSuperGroup(FKITSuperGroup superGroup) {
        this.superGroup = superGroup;
    }

    public FKITGroup getGroup() {
        return this.group;
    }

    public void setGroup(FKITGroup group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FKITGroupToSuperGroupPK that = (FKITGroupToSuperGroupPK) o;
        return Objects.equals(this.superGroup, that.superGroup)
            && Objects.equals(this.group, that.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.superGroup, this.group);
    }

    @Override
    public String toString() {
        return "FKITGroupToSuperGroupPK{"
            + "superGroup=" + this.superGroup
            + ", group=" + this. group
            + '}';
    }
}
