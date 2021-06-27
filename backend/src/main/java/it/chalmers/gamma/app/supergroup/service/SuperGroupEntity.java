package it.chalmers.gamma.app.supergroup.service;

import it.chalmers.gamma.app.domain.EntityName;
import it.chalmers.gamma.app.domain.PrettyName;
import it.chalmers.gamma.app.domain.SuperGroup;
import it.chalmers.gamma.app.domain.SuperGroupId;
import it.chalmers.gamma.app.domain.SuperGroupType;
import it.chalmers.gamma.app.domain.Email;
import it.chalmers.gamma.app.service.TextEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;

import javax.persistence.*;


@Entity
@Table(name = "fkit_super_group")
public class SuperGroupEntity extends MutableEntity<SuperGroupId, SuperGroup> {

    @EmbeddedId
    private SuperGroupId id;

    @JoinColumn(name = "description")
    @OneToOne(cascade = CascadeType.MERGE)
    private TextEntity description;

    @Embedded
    private EntityName entityName;

    @Embedded
    private PrettyName prettyName;

    @Embedded
    private SuperGroupType superGroupType;

    @Embedded
    private Email email;

    protected SuperGroupEntity() {}

    protected SuperGroupEntity(SuperGroup sg) {
        assert(sg.id() != null);

        this.id = sg.id();
        this.description = new TextEntity();

        apply(sg);
    }

    @Override
    public void apply(SuperGroup sg)  {
        assert(this.id == sg.id());

        this.email = sg.email();
        this.entityName = sg.name();
        this.prettyName = sg.prettyName();
        this.superGroupType = sg.type();


        this.description.apply(sg.description());
    }

    @Override
    protected SuperGroup toDTO() {
        return new SuperGroup(
                this.id,
                this.entityName,
                this.prettyName,
                this.superGroupType,
                this.email,
                this.description.toDTO()
        );
    }

    @Override
    protected SuperGroupId id() {
        return this.id;
    }
}
