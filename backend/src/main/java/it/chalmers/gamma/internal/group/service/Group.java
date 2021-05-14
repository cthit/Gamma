package it.chalmers.gamma.internal.group.service;

import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupId;
import it.chalmers.gamma.util.domain.abstraction.Id;

import javax.persistence.*;

@Entity
@Table(name = "fkit_group")
public class Group extends MutableEntity<GroupId, GroupShallowDTO> {

    @EmbeddedId
    private GroupId id;

    @Column(name = "name")
    private String name;

    @Column(name = "pretty_name")
    private String prettyName;

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

    @Override
    public void apply(GroupShallowDTO g) {
        assert(this.id == g.id());

        this.name = g.name();
        this.prettyName = g.prettyName();
        this.email = g.email();
        this.superGroupId = g.superGroupId();
    }

    @Override
    protected GroupShallowDTO toDTO() {
        return new GroupShallowDTO(
                this.id,
                this.email,
                this.name,
                this.prettyName,
                this.superGroupId
        );
    }

    @Override
    protected GroupId id() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Group{"
                + "id=" + this.id
                + ", name='" + this.name + '\''
                + ", prettyName='" + this.prettyName + '\''
                + ", email=" + this.email + '\''
                + ", superGroup='" + this.superGroupId
                + '}';
    }

}