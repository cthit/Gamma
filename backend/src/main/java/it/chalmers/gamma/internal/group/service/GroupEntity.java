package it.chalmers.gamma.internal.group.service;

import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.GroupId;
import it.chalmers.gamma.domain.EntityName;
import it.chalmers.gamma.domain.PrettyName;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;
import it.chalmers.gamma.domain.SuperGroupId;

import javax.persistence.*;

@Entity
@Table(name = "fkit_group")
public class GroupEntity extends MutableEntity<GroupId, GroupShallowDTO> {

    @EmbeddedId
    private GroupId id;

    @Embedded
    private EntityName name;

    @Embedded
    private PrettyName prettyName;

    @Column(name = "email")
    private Email email;

    @Embedded
    private SuperGroupId superGroupId;

    protected GroupEntity() {}

    protected GroupEntity(GroupShallowDTO g) {
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

}