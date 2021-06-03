package it.chalmers.gamma.internal.supergroup.service;

import it.chalmers.gamma.domain.EntityName;
import it.chalmers.gamma.domain.PrettyName;
import it.chalmers.gamma.domain.SuperGroupId;
import it.chalmers.gamma.domain.SuperGroupType;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.internal.supergroup.type.service.SuperGroupTypeEntity;
import it.chalmers.gamma.internal.text.service.TextEntity;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;

import javax.persistence.*;


@Entity
@Table(name = "fkit_super_group")
public class SuperGroupEntity extends MutableEntity<SuperGroupId, SuperGroupDTO> {

    @EmbeddedId
    private SuperGroupId id;

    @JoinColumn(name = "description")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private TextEntity description;

    @Embedded
    private EntityName name;

    @Embedded
    private PrettyName prettyName;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private SuperGroupType type;

    @Embedded
    private Email email;

    protected SuperGroupEntity() {}

    protected SuperGroupEntity(SuperGroupDTO sg) {
        assert(sg.id() != null);

        this.id = sg.id();
        this.description = new TextEntity();

        apply(sg);
    }

    @Override
    public void apply(SuperGroupDTO sg)  {
        assert(this.id == sg.id());

        this.email = sg.email();
        this.name = sg.name();
        this.prettyName = sg.prettyName();
        this.type = sg.type();


        this.description.apply(sg.description());
    }

    @Override
    protected SuperGroupDTO toDTO() {
        return new SuperGroupDTO(
                this.id,
                this.name,
                this.prettyName,
                this.type,
                this.email,
                this.description.toDTO()
        );
    }

    @Override
    protected SuperGroupId id() {
        return this.id;
    }
}
