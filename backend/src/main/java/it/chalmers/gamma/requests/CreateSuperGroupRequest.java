package it.chalmers.gamma.requests;

import it.chalmers.gamma.domain.GroupType;

import java.util.Objects;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class CreateSuperGroupRequest {
    @NotNull
    @Size(min = 2, max = 50)
    private String name;
    @Max(50)
    private String prettyName;
    @NotNull
    private GroupType type;

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
        return "CreateSuperGroupRequest{"
                + "name='" + this.name + '\''
                + ", prettyName='" + this.prettyName + '\''
                + ", type=" + this.type
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateSuperGroupRequest that = (CreateSuperGroupRequest) o;
        return Objects.equals(this.name, that.name)
                && Objects.equals(this.prettyName, that.prettyName)
                && this.type == that.type;
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.name, this.prettyName, this.type);
    }
}
