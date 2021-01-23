package it.chalmers.gamma.supergroup;

import it.chalmers.gamma.domain.GroupType;

import java.util.Objects;
import java.util.UUID;

public class SuperGroupDTO {

    private final UUID id;
    private final String name;
    private final String prettyName;
    private final GroupType type;
    private final String email;

    public SuperGroupDTO(UUID id, String name, String prettyName, GroupType type, String email) {
        this.id = id;
        this.name = name;
        this.prettyName = prettyName;
        this.type = type;
        this.email = email;
    }

    public SuperGroupDTO(String name, String prettyName, GroupType type, String email) {
        this(null, name, prettyName, type, email);
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getPrettyName() {
        return this.prettyName;
    }

    public GroupType getType() {
        return this.type;
    }

    public String getEmail() {
        return this.email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SuperGroupDTO that = (SuperGroupDTO) o;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.name, that.name)
                && Objects.equals(this.prettyName, that.prettyName)
                && this.type == that.type
                && Objects.equals(this.email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name, this.prettyName, this.type, this.email);
    }

    @Override
    public String toString() {
        return "FKITSuperGroupDTO{"
                + "id=" + this.id
                + ", name='" + this.name + '\''
                + ", prettyName='" + this.prettyName + '\''
                + ", type=" + this.type
                + ", email='" + this.email + '\''
                + '}';
    }
}
