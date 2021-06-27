package it.chalmers.gamma.app.group.service;

import it.chalmers.gamma.app.domain.Email;
import it.chalmers.gamma.app.domain.GroupId;
import it.chalmers.gamma.app.domain.EntityName;
import it.chalmers.gamma.app.domain.PrettyName;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
import it.chalmers.gamma.app.domain.SuperGroupId;

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