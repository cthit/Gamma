package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.app.domain.Email;
import it.chalmers.gamma.app.domain.Group;
import it.chalmers.gamma.app.domain.GroupId;
import it.chalmers.gamma.app.domain.Name;
import it.chalmers.gamma.app.domain.PrettyName;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "fkit_group")
public class GroupEntity extends MutableEntity<GroupId, Group> {

    @Id
    @Column(name = "group_id")
    private UUID id;

    @Column(name = "e_name")
    private String name;

    @Column(name = "pretty_name")
    private String prettyName;

    @Column(name = "email")
    private String email;

    @ManyToOne
    @JoinColumn(name = "super_group_id")
    private SuperGroupEntity superGroup;

    protected GroupEntity() {}

    protected GroupEntity(Group g) {
        assert(g.id() != null);

        this.id = g.id().value();

        apply(g);
    }

    @Override
    public void apply(Group g) {
        assert(this.id == g.id().value());

        this.name = g.name().value();
        this.prettyName = g.prettyName().value();
        this.email = g.email().value();
        this.superGroup = new SuperGroupEntity(g.superGroup());
    }

    @Override
    protected Group toDTO() {
        return new Group(
                GroupId.valueOf(this.id),
                new Email(this.email),
                new Name(this.name),
                new PrettyName(this.prettyName),
                this.superGroup.toDTO()
        );
    }

    @Override
    protected GroupId id() {
        return GroupId.valueOf(this.id);
    }

}