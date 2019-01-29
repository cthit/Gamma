package it.chalmers.gamma.db.entity;

import it.chalmers.gamma.db.entity.pk.FKITGroupToSuperGroupPK;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "fkit_group_to_super_group")
public class FKITGroupToSuperGroup {
    @EmbeddedId
    private FKITGroupToSuperGroupPK id;

    public FKITGroupToSuperGroup(FKITGroupToSuperGroupPK id) {
        this.id = id;
    }
    public FKITGroupToSuperGroup(){

    }

    public FKITGroupToSuperGroupPK getId() {
        return id;
    }

    public void setId(FKITGroupToSuperGroupPK id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FKITGroupToSuperGroup that = (FKITGroupToSuperGroup) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "FKITGroupToSuperGroup{" +
                "id=" + id +
                '}';
    }
}
