package it.chalmers.gamma.requests;

import it.chalmers.gamma.domain.GroupType;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

public class CreateSuperGroupRequest {
    @NotNull
    @Size(min = 2, max = 50)
    private String name;
    @Max(50)
    private String prettyName;
    @NotNull
    private GroupType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrettyName() {
        return prettyName;
    }

    public void setPrettyName(String prettyName) {
        this.prettyName = prettyName;
    }

    public GroupType getType() {
        return type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "CreateSuperGroupRequest{" +
                "name='" + name + '\'' +
                ", prettyName='" + prettyName + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        CreateSuperGroupRequest that = (CreateSuperGroupRequest) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(prettyName, that.prettyName) &&
                type == that.type;
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, prettyName, type);
    }
}
