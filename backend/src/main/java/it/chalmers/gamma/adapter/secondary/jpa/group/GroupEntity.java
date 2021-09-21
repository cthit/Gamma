package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.domain.common.Email;
import it.chalmers.gamma.domain.group.Group;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.user.Name;
import it.chalmers.gamma.domain.common.PrettyName;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "fkit_group")
public class GroupEntity extends MutableEntity<GroupId> {

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

        this.id = g.id().getValue();

        apply(g);
    }

    public void apply(Group g) {
        assert(this.id == g.id().getValue());

        this.name = g.name().value();
        this.prettyName = g.prettyName().value();
        this.email = g.email().value();
        this.superGroup = new SuperGroupEntity(g.superGroup());
    }

    public Group toDomain() {
        //TODO: link imageuri
        // TODO: add member
        return new Group(
                new GroupId(this.id),
                new Email(this.email),
                new Name(this.name),
                new PrettyName(this.prettyName),
                this.superGroup.toDomain(),
                null,
                null,
                null
        );
    }

    @Override
    protected GroupId id() {
        return new GroupId(this.id);
    }

}