package it.chalmers.gamma.domain.supergroup.data;

import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.BaseEntity;
import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.domain.MutableEntity;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.domain.text.data.db.Text;

import javax.persistence.*;


@Entity
@Table(name = "fkit_super_group")
public class SuperGroup implements MutableEntity<SuperGroupDTO> {

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
    private GroupType type;

    @Embedded
    private Email email;

    protected SuperGroup() {}

    public SuperGroup(SuperGroupDTO sg) {
        assert(sg.getId() != null);

        this.id = sg.getId();
        this.description = new Text();

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
    public void apply(SuperGroupDTO sg)  {
        assert(this.id == sg.getId());

        this.email = sg.getEmail();
        this.name = sg.getName();
        this.prettyName = sg.getPrettyName();
        this.type = sg.getType();
        this.description.apply(sg.getDescription());
    }

    @Override
    public SuperGroupDTO toDTO() {
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