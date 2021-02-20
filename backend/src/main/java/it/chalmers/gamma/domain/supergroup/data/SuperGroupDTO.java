package it.chalmers.gamma.domain.supergroup.data;

import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.domain.text.Text;

import java.util.Objects;
import java.util.UUID;

public class SuperGroupDTO {

    private final SuperGroupId id;
    private final String name;
    private final String prettyName;
    private final GroupType type;
    private final String email;
    private final Text description;

    public SuperGroupDTO(SuperGroupId id,
                         String name,
                         String prettyName,
                         GroupType type,
                         String email,
                         Text description) {
        this.id = id;
        this.name = name;
        this.prettyName = prettyName;
        this.type = type;
        this.email = email;
        this.description = description;
    }

    public SuperGroupDTO(String name,
                         String prettyName,
                         GroupType type,
                         String email,
                         Text description) {
        this(null, name, prettyName, type, email, description);
    }

    public SuperGroupId getId() {
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

    public Text getDescription() {
        return description;
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
        return "SuperGroupDTO{"
                + "id=" + this.id
                + ", name='" + this.name + '\''
                + ", prettyName='" + this.prettyName + '\''
                + ", type=" + this.type
                + ", email='" + this.email + '\''
                + '}';
    }
}
