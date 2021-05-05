package it.chalmers.gamma.domain.supergroup.service;

import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;
import it.chalmers.gamma.domain.text.data.db.Text;

import javax.persistence.*;


@Entity
@Table(name = "fkit_super_group")
public class SuperGroup extends MutableEntity<SuperGroupDTO> {

    @EmbeddedId
    private SuperGroupId id;

    @JoinColumn(name = "description")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Text description;

    @Column(name = "name")
    private String name;

    @Column(name = "pretty_name")
    private String prettyName;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private SuperGroupType type;

    @Embedded
    private Email email;

    protected SuperGroup() {}

    protected SuperGroup(SuperGroupDTO sg) {
        assert(sg.id() != null);

        this.id = sg.id();

        if(this.description == null) {
            this.description = new Text();
        }

        apply(sg);
    }

    @Override
    public String toString() {
        return "SuperGroup{"
                + "id=" + this.id
                + ", name='" + this.name + '\''
                + ", prettyName='" + this.prettyName + '\''
                + ", type=" + this.type
                + ", email='" + this.email + '\''
                + '}';
    }

    @Override
    protected void apply(SuperGroupDTO sg)  {
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
                id,
                name,
                prettyName,
                type,
                email,
                description.toDTO()
        );
    }
}
