package it.chalmers.gamma.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.chalmers.gamma.domain.GroupType;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "fkit_group")
public class FKITGroup {

    @Id
    @Column(updatable = false)
    @JsonIgnore
    private UUID id;

    @Column(name = "avatar_url")
    private String avatarURL;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "pretty_name", length = 50, nullable = false)
    private String prettyName;

    @JoinColumn(name = "description")
    @OneToOne
    private Text description;

    @JoinColumn(name = "function", nullable = false)
    @OneToOne
    private Text function;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "type", length = 30, nullable = false)
    @Enumerated(EnumType.STRING)
    private GroupType type;

    public FKITGroup() {
        id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Text getDescription() {
        return description;
    }

    public void setDescription(Text description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public GroupType getType() {
        return type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public Text getFunction() {
        return function;
    }

    public void setFunction(Text function) {
        this.function = function;
    }

    public String getPrettyName() {
        return prettyName;
    }

    public void setPrettyName(String prettyName) {
        this.prettyName = prettyName;
    }

    @Override
    public String toString() {
        return "FKITGroup{" +
                "id=" + id +
                ", avatarURL='" + avatarURL + '\'' +
                ", name='" + name + '\'' +
                ", prettyName='" + prettyName + '\'' +
                ", description=" + description +
                ", function=" + function +
                ", email='" + email + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FKITGroup fkitGroup = (FKITGroup) o;
        return Objects.equals(id, fkitGroup.id) &&
                Objects.equals(avatarURL, fkitGroup.avatarURL) &&
                Objects.equals(name, fkitGroup.name) &&
                Objects.equals(prettyName, fkitGroup.prettyName) &&
                Objects.equals(description, fkitGroup.description) &&
                Objects.equals(function, fkitGroup.function) &&
                Objects.equals(email, fkitGroup.email) &&
                type == fkitGroup.type;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, avatarURL, name, prettyName, description, function, email, type);
    }

}
