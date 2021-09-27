package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.app.domain.common.Email;
import it.chalmers.gamma.app.domain.group.GroupId;
import it.chalmers.gamma.app.domain.supergroup.SuperGroup;
import it.chalmers.gamma.app.domain.user.Name;
import it.chalmers.gamma.app.domain.common.PrettyName;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "fkit_group")
public class GroupEntity extends MutableEntity<GroupId> {

    @Id
    @Column(name = "group_id")
    public UUID id;

    @Column(name = "e_name")
    public String name;

    @Column(name = "pretty_name")
    public String prettyName;

    @Column(name = "email")
    public String email;

    @ManyToOne
    @JoinColumn(name = "super_group_id")
    public SuperGroupEntity superGroup;

    @OneToMany(mappedBy = "id.group", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<MembershipEntity> members;

    protected GroupEntity() {}

    public List<MembershipEntity> getMembers() {
        return members;
    }

    public record GroupBase(
            GroupId groupId,
            Email email,
            Name name,
            PrettyName prettyName,
            SuperGroup superGroup
    ) { }

    public GroupBase toDomain() {
        return new GroupBase(
                new GroupId(this.id),
                new Email(this.email),
                new Name(this.name),
                new PrettyName(this.prettyName),
                this.superGroup.toDomain()
        );
    }

    @Override
    protected GroupId id() {
        return new GroupId(this.id);
    }

}