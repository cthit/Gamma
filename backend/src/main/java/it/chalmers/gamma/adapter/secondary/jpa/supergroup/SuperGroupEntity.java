package it.chalmers.gamma.adapter.secondary.jpa.supergroup;

import it.chalmers.gamma.app.domain.user.Name;
import it.chalmers.gamma.app.domain.common.PrettyName;
import it.chalmers.gamma.app.domain.supergroup.SuperGroup;
import it.chalmers.gamma.app.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.app.domain.supergroup.SuperGroupType;
import it.chalmers.gamma.app.domain.common.Email;
import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;

import javax.persistence.*;
import java.util.UUID;


@Entity
@Table(name = "fkit_super_group")
public class SuperGroupEntity extends MutableEntity<SuperGroupId> {

    @Id
    @Column(name = "super_group_id")
    private UUID id;

    @JoinColumn(name = "description")
    @OneToOne(cascade = CascadeType.MERGE)
    private TextEntity description;

    @Column(name = "e_name")
    private String name;

    @Column(name = "pretty_name")
    private String prettyName;

    @Column(name = "super_group_type_name")
    private String superGroupType;

    @Column(name = "email")
    private String email;

    public SuperGroupEntity() {}

    public SuperGroupEntity(SuperGroup sg) {
        assert(sg.id() != null);

        this.id = sg.id().getValue();
        this.description = new TextEntity();

        apply(sg);
    }

    public void apply(SuperGroup sg)  {
        assert(this.id == sg.id().getValue());

        this.email = sg.email().value();
        this.name = sg.name().value();
        this.prettyName = sg.prettyName().value();
        this.superGroupType = sg.type().getValue();


        this.description.apply(sg.description());
    }

    public SuperGroup toDomain() {
        return new SuperGroup(
                new SuperGroupId(this.id),
                new Name(this.name),
                new PrettyName(this.prettyName),
                SuperGroupType.valueOf(this.superGroupType),
                new Email(this.email),
                this.description.toDomain()
        );
    }

    @Override
    protected SuperGroupId id() {
        return new SuperGroupId(this.id);
    }
}
