package it.chalmers.gamma.domain.supergroup.data;

import it.chalmers.gamma.domain.GEntity;
import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.domain.IDsNotMatchingException;
import it.chalmers.gamma.domain.text.Text;

import java.util.UUID;

import javax.persistence.*;


@Entity
@Table(name = "fkit_super_group")
public class SuperGroup implements GEntity<SuperGroupDTO> {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "pretty_name")
    private String prettyName;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private GroupType type;

    @Column(name = "email")
    private String email;

    @JoinColumn(name = "description")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Text description;

    protected SuperGroup() {}

    public SuperGroup(SuperGroupDTO sg) {
        this.id = sg.getId();
        try {
            apply(sg);
        } catch (IDsNotMatchingException ignored) {}

        if(this.id == null) {
            this.id = UUID.randomUUID();
        }

    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name.toLowerCase();
    }

    public String getPrettyName() {
        return this.prettyName;
    }

    public void setPrettyName(String prettyName) {
        this.prettyName = prettyName;
    }

    public GroupType getType() {
        return this.type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    public Text getDescription() {
        return description;
    }

    public void setDescription(Text description) {
        this.description = description;
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
    public void apply(SuperGroupDTO sg) throws IDsNotMatchingException {
        if(this.id != null && this.id != sg.getId()) {
            throw new IDsNotMatchingException();
        }

        this.id = sg.getId();
        this.email = sg.getEmail();
        this.name = sg.getName();
        this.prettyName = sg.getPrettyName();
        this.type = sg.getType();
        this.description = sg.getDescription();
    }
}
