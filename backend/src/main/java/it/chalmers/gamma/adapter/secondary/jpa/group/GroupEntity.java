package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.app.domain.common.Email;
import it.chalmers.gamma.app.domain.group.GroupId;
import it.chalmers.gamma.app.domain.supergroup.SuperGroup;
import it.chalmers.gamma.app.domain.user.Name;
import it.chalmers.gamma.app.domain.common.PrettyName;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "fkit_group")
public class GroupEntity extends MutableEntity<GroupId> {

    @Id
    @Column(name = "group_id")
    protected UUID id;

    @Column(name = "e_name")
    protected String name;

    @Column(name = "pretty_name")
    protected String prettyName;

    @Column(name = "email")
    protected String email;

    @ManyToOne
    @JoinColumn(name = "super_group_id")
    protected SuperGroupEntity superGroup;

    @OneToMany(mappedBy = "id.group", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<MembershipEntity> members;

    //TODO Add avatar and banner

    protected GroupEntity() {
        this.members = new ArrayList<>();
    }

    protected List<MembershipEntity> getMembers() {
        return members;
    }

    @Override
    public GroupId id() {
        return new GroupId(this.id);
    }

    public SuperGroupEntity getSuperGroup() {
        return this.superGroup;
    }

    public String getName() {
        return this.name;
    }

}