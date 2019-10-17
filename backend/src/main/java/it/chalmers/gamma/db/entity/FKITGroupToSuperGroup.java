package it.chalmers.delta.db.entity;

import it.chalmers.delta.db.entity.pk.FKITGroupToSuperGroupPK;

import java.util.Objects;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "fkit_group_to_super_group")
public class FKITGroupToSuperGroup {
    @EmbeddedId
    private FKITGroupToSuperGroupPK id;

    public FKITGroupToSuperGroup(FKITGroupToSuperGroupPK id) {
        this.id = id;
    }

    public FKITGroupToSuperGroup() {
        //Used by hibernate
    }

    public FKITGroupToSuperGroupPK getId() {
        return this.id;
    }

    public void setId(FKITGroupToSuperGroupPK id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FKITGroupToSuperGroup that = (FKITGroupToSuperGroup) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "FKITGroupToSuperGroup{"
            + "id=" + this.id
            + '}';
    }
}
