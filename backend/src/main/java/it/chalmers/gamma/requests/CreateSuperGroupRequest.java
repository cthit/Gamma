package it.chalmers.gamma.requests;

import it.chalmers.gamma.domain.GroupType;

import java.util.Objects;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class CreateSuperGroupRequest {

    @NotNull(message = "NAME_MUST_BE_PROVIDED")
    @Size(min = 2, max = 50, message = "NAME_MUST_BE_BETWEEN_2_AND_50_CHARACTERS")
    private String name;

    @Size(max = 50, message = "PRETTY_NAME_TOO_LONG")
    private String prettyName;

    @NotNull(message = "TYPE_MUST_BE_PROVIDED")
    private GroupType type;

    @NotNull(message = "EMAIL_MUST_BE_PROVIDED")
    @Email(message = "FIELD_EMAIL_DOES_NOT_MATCH_EMAIL_ADDRESS")
    private String email;


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
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateSuperGroupRequest that = (CreateSuperGroupRequest) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(prettyName, that.prettyName) &&
                type == that.type &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, prettyName, type, email);
    }

    @Override
    public String toString() {
        return "CreateSuperGroupRequest{" +
                "name='" + name + '\'' +
                ", prettyName='" + prettyName + '\'' +
                ", type=" + type +
                ", email='" + email + '\'' +
                '}';
    }

}
