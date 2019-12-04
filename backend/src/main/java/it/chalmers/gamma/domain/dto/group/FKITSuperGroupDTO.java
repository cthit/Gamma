package it.chalmers.gamma.domain.dto.group;

import it.chalmers.gamma.domain.GroupType;

import java.util.Objects;
import java.util.UUID;

public class FKITSuperGroupDTO {

    private final UUID id;
    private final String name;
    private final String prettyName;
    private final GroupType type;
    private final String email;

    public FKITSuperGroupDTO(UUID id, String name, String prettyName, GroupType type, String email) {
        this.id = id;
        this.name = name;
        this.prettyName = prettyName;
        this.type = type;
        this.email = email;
    }

    public FKITSuperGroupDTO(String name, String prettyName, GroupType type, String email) {
        this(null, name, prettyName, type, email);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrettyName() {
        return prettyName;
    }

    public GroupType getType() {
        return type;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FKITSuperGroupDTO that = (FKITSuperGroupDTO) o;
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

    @Override
    public String toString() {
        return "FKITSuperGroupDTO{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", prettyName='" + prettyName + '\'' +
            ", type=" + type +
            ", email='" + email + '\'' +
            '}';
    }
}
