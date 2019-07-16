package it.chalmers.gamma.db.entity;

import it.chalmers.gamma.domain.GroupType;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
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
    private GroupType type;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    @Override
    public String toString() {
        return "FKITSuperGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", prettyName='" + prettyName + '\'' +
                ", type=" + type +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FKITSuperGroup that = (FKITSuperGroup) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(prettyName, that.prettyName) &&
                type == that.type &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, prettyName, type, email);
    }
}
