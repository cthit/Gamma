package it.chalmers.gamma.internal.group.service;

import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.util.domain.abstraction.BaseEntity;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupId;

import java.util.Calendar;

import javax.persistence.*;

@Entity
@Table(name = "fkit_group")
public class Group extends BaseEntity<GroupShallowDTO> {

    @EmbeddedId
    private GroupId id;

    @Column(name = "name")
    private String name;

    @Column(name = "pretty_name")
    private String prettyName;

    @Column(name = "becomes_active")
    private Calendar becomesActive;

    @Column(name = "becomes_inactive")
    private Calendar becomesInactive;

    @Column(name = "email")
    private Email email;

    @Embedded
    private SuperGroupId superGroupId;

    protected Group() {}

    protected Group(GroupShallowDTO g) {
        assert(g.id() != null);

        this.id = g.id();

        apply(g);
    }

    protected void apply(GroupShallowDTO g) {
        assert(this.id == g.id());

        this.name = g.name();
        this.prettyName = g.prettyName();
        this.becomesActive = g.becomesActive();
        this.becomesInactive = g.becomesInactive();
        this.email = g.email();
        this.superGroupId = g.superGroupId();
    }

    @Override
    protected GroupShallowDTO toDTO() {
        return new GroupShallowDTO(
                this.id,
                this.becomesActive,
                this.becomesInactive,
                this.email,
                this.name,
                this.prettyName,
                this.superGroupId
        );
    }

    @Override
    public String toString() {
        return "Group{"
                + "id=" + this.id
                + ", name='" + this.name + '\''
                + ", prettyName='" + this.prettyName + '\''
                + ", becomesActive=" + this.becomesActive
                + ", becomesInactive=" + this.becomesInactive
                + ", email=" + this.email + '\''
                + ", superGroup='" + this.superGroupId
                + '}';
    }

}