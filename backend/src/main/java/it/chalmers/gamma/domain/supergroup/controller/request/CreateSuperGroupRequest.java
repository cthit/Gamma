package it.chalmers.gamma.domain.supergroup.controller.request;

import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.domain.supergroup.SuperGroupType;
import it.chalmers.gamma.domain.text.data.dto.TextDTO;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class CreateSuperGroupRequest {

    @NotNull(message = "NAME_MUST_BE_PROVIDED")
    @Size(min = 2, max = 50, message = "NAME_MUST_BE_BETWEEN_2_AND_50_CHARACTERS")
    private String name;

    @Size(max = 50, message = "PRETTY_NAME_TOO_LONG")
    private String prettyName;

    @NotNull(message = "TYPE_MUST_BE_PROVIDED")
    private SuperGroupType type;

    @NotNull(message = "EMAIL_MUST_BE_PROVIDED")
    private Email email;

    private TextDTO description;

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

    public SuperGroupType getType() {
        return this.type;
    }

    public void setType(SuperGroupType type) {
        this.type = type;
    }

    public Email getEmail() {
        return this.email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public TextDTO getDescription() {
        return description;
    }

    public void setDescription(TextDTO description) {
        this.description = description;
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
                && this.type == that.type
                && Objects.equals(this.email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.prettyName, this.type, this.email);
    }

    @Override
    public String toString() {
        return "CreateSuperGroupRequest{"
                + "name='" + this.name + '\''
                + ", prettyName='" + this.prettyName + '\''
                + ", type=" + this.type
                + ", email='" + this.email + '\''
                + '}';
    }

}
