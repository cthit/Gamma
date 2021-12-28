package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.app.group.domain.GroupId;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "fkit_group")
public class GroupEntity extends MutableEntity<UUID> {

    @Id
    @Column(name = "group_id")
    protected UUID id;

    @Column(name = "e_name")
    protected String name;

    @Column(name = "pretty_name")
    protected String prettyName;

    @ManyToOne
    @JoinColumn(name = "super_group_id")
    protected SuperGroupEntity superGroup;

    @OneToMany(mappedBy = "id.group", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<MembershipEntity> members;

    @OneToOne(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    protected GroupImagesEntity groupImages;

    protected GroupEntity() { }

    protected List<MembershipEntity> getMembers() {
        return members;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    public SuperGroupEntity getSuperGroup() {
        return this.superGroup;
    }

    public String getName() {
        return this.name;
    }

}