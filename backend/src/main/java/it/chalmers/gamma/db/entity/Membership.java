package it.chalmers.gamma.db.entity;

import it.chalmers.gamma.db.entity.pk.MembershipPK;
import it.chalmers.gamma.domain.dto.membership.MembershipDTO;

import java.util.List;
import java.util.Objects;

import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "membership")
public class Membership {

    @EmbeddedId
    private MembershipPK id;

    @Column(name = "unofficial_post_name", length = 100)
    private String unofficialPostName;

    // This enables easy and safe transport of a groups superGroup, without storing it in the database.
    @Transient
    private List<FKITSuperGroup> fkitSuperGroups;



    public MembershipPK getId() {
        return this.id;
    }

    public void setId(MembershipPK id) {
        this.id = id;
    }

    public String getUnofficialPostName() {
        return this.unofficialPostName;
    }

    public void setUnofficialPostName(String unofficialPostName) {
        this.unofficialPostName = unofficialPostName;
    }

    public List<FKITSuperGroup> getFkitSuperGroups() {
        return this.fkitSuperGroups;
    }

    public void setFkitSuperGroups(List<FKITSuperGroup> fkitSuperGroups) {
        this.fkitSuperGroups = fkitSuperGroups;
    }

    public MembershipDTO toDTO(){
        return new MembershipDTO(
                this.id.getPost().toDTO(),
                id.getFKITGroup().toDTO(),
                this.unofficialPostName,
                this.id.getITUser().toDTO());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Membership that = (Membership) o;
        return Objects.equals(this.id, that.id)
            && Objects.equals(this.unofficialPostName, that.unofficialPostName)
            && Objects.equals(this.fkitSuperGroups, that.fkitSuperGroups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.unofficialPostName, this.fkitSuperGroups);
    }

    @Override
    public String toString() {
        return "Membership{"
            + "id=" + this.id
            + ", unofficialPostName='" + this.unofficialPostName + '\''
            + ", fkitSuperGroups='" + this.fkitSuperGroups + '\''
            + '}';
    }

}
