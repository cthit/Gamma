package it.chalmers.gamma.group.controller.request;

import it.chalmers.gamma.domain.text.Text;

import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@SuppressWarnings("PMD.ExcessiveParameterList")
public class CreateOrEditGroupRequest {

    @NotNull(message = "NAME_MUST_BE_PROVIDED")
    @Size(max = 50, message = "NAME_TOO_LONG")
    private String name;

    @Size(max = 50, message = "PRETTY_NAME_TOO_LONG")
    private String prettyName;

    private Text description;

    @NotNull(message = "A_FUNCTION_MUST_BE_PROVIDED")
    private Text function;
    private String avatarURL;

    @NotNull(message = "BECOMES_ACTIVE_MUST_BE_PROVIDED")       // MORE SPECIFIC CHECK
    private Calendar becomesActive;

    @NotNull(message = "BECOMES_INACTIVE_MUST_BE_PROVIDED")       // MORE SPECIFIC CHECK
    private Calendar becomesInactive;

    private UUID superGroup;

    private String email;

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

    public Text getDescription() {
        return description;
    }

    public void setDescription(Text description) {
        this.description = description;
    }

    public Text getFunction() {
        return function;
    }

    public void setFunction(Text function) {
        this.function = function;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public Calendar getBecomesActive() {
        return becomesActive;
    }

    public void setBecomesActive(Calendar becomesActive) {
        this.becomesActive = becomesActive;
    }

    public Calendar getBecomesInactive() {
        return becomesInactive;
    }

    public void setBecomesInactive(Calendar becomesInactive) {
        this.becomesInactive = becomesInactive;
    }

    public UUID getSuperGroupId() {
        return superGroup;
    }

    public void setSuperGroup(UUID superGroup) {
        this.superGroup = superGroup;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateOrEditGroupRequest that = (CreateOrEditGroupRequest) o;
        return Objects.equals(this.name, that.name)
                && Objects.equals(this.prettyName, that.prettyName)
                && Objects.equals(this.description, that.description)
                && Objects.equals(this.function, that.function)
                && Objects.equals(this.avatarURL, that.avatarURL)
                && Objects.equals(this.becomesActive, that.becomesActive)
                && Objects.equals(this.becomesInactive, that.becomesInactive)
                && Objects.equals(this.superGroup, that.superGroup)
                && Objects.equals(this.email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name,
                this.prettyName,
                this.description,
                this.function,
                this.avatarURL,
                this.becomesActive,
                this.becomesInactive,
                this.superGroup,
                this.email);

    }

    @Override
    public String toString() {
        return "CreateGroupRequest{"
                + "name='" + this.name + '\''
                + ", prettyName='" + this.prettyName + '\''
                + ", description=" + this.description
                + ", function=" + this.function
                + ", avatarURL='" + this.avatarURL + '\''
                + ", becomesActive=" + this.becomesActive
                + ", becomesInactive=" + this.becomesInactive
                + ", superGroup='" + this.superGroup + '\''
                + ", email='" + this.email + '\''
                + '}';
    }
}
