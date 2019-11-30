package it.chalmers.gamma.db.entity;

import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "fkit_super_group")

public class FKITSuperGroup {

    @Column(name = "id")
    @Id
    private final UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "pretty_name")
    private String prettyName;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private GroupType type;

    @Column(name = "email")
    private String email;

    public FKITSuperGroup() {
        this.id = UUID.randomUUID();
    }

    public FKITSuperGroup(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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
        this.email = email;
    }

    public FKITSuperGroupDTO toDTO(){
        return new FKITSuperGroupDTO(
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
}
