package it.chalmers.gamma.supergroup;

import it.chalmers.gamma.domain.GEntity;
import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.domain.IDsNotMatchingException;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "fkit_super_group")

public class SuperGroup implements GEntity<SuperGroupDTO> {

    @Column(name = "id", updatable = false)
    @Id
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

    public SuperGroup() {
        this.id = UUID.randomUUID();
    }

    public SuperGroup(UUID id) {
        this.id = id;
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

    public SuperGroupDTO toDTO() {
        return new SuperGroupDTO(
            this.id, this.name, this.prettyName, this.type, this.email
        );
    }

    @Override
    public String toString() {
        return "FKITSuperGroup{"
                + "id=" + this.id
                + ", name='" + this.name + '\''
                + ", prettyName='" + this.prettyName + '\''
                + ", type=" + this.type
                + ", email='" + this.email + '\''
                + '}';
    }

    @Override
    public void apply(SuperGroupDTO sg) throws IDsNotMatchingException {
        if(this.id != sg.getId()) {
            throw new IDsNotMatchingException();
        }

        this.id = sg.getId();
        this.email = sg.getEmail();
        this.name = sg.getName();
        this.prettyName = sg.getPrettyName();
        this.type = sg.getType();
    }
}
