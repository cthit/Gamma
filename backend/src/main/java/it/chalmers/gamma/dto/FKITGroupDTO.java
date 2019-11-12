package it.chalmers.gamma.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.views.WebsiteView;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("PMD.ExcessiveParameterList")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FKITGroupDTO {

    private final UUID id;
    private final Calendar becomesActive;
    private final Calendar becomesInactive;
    private final Text description;
    private final String email;
    private final Text function;
    private final boolean active;
    private final String name;
    private final String prettyName;
    private final List<MembershipDTO> groupMembers;
    private final List<FKITSuperGroup> superGroup;
    private final List<WebsiteView> websites;


    public FKITGroupDTO(UUID id,
                        Calendar becomesActive,
                        Calendar becomesInactive,
                        Text description,
                        String email,
                        Text function,
                        Boolean isActive,
                        String name,
                        String prettyName,
                        List<MembershipDTO> groupMembers,
                        List<FKITSuperGroup> superGroup,
                        List<WebsiteView> websites) {
        this.id = id;
        this.becomesActive = becomesActive;
        this.becomesInactive = becomesInactive;
        this.description = description;
        this.email = email;
        this.function = function;
        this.active = isActive;
        this.name = name;
        this.prettyName = prettyName;
        this.groupMembers = groupMembers;
        this.superGroup = superGroup;
        this.websites = websites;
    }

    public UUID getId() {
        return this.id;
    }

    public Calendar getBecomesActive() {
        return this.becomesActive;
    }

    public Calendar getBecomesInactive() {
        return this.becomesInactive;
    }

    public Text getDescription() {
        return this.description;
    }

    public String getEmail() {
        return this.email;
    }

    public Text getFunction() {
        return this.function;
    }

    public boolean isActive() {
        return this.active;
    }

    public String getName() {
        return this.name;
    }

    public String getPrettyName() {
        return this.prettyName;
    }

    public List<MembershipDTO> getGroupMembers() {
        return this.groupMembers;
    }

    public List<FKITSuperGroup> getSuperGroup() {
        return this.superGroup;
    }

    public List<WebsiteView> getWebsites() {
        return this.websites;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FKITGroupDTO that = (FKITGroupDTO) o;
        return this.active == that.active
                && Objects.equals(this.id, that.id)
                && Objects.equals(this.becomesActive, that.becomesActive)
                && Objects.equals(this.becomesInactive, that.becomesInactive)
                && Objects.equals(this.description, that.description)
                && Objects.equals(this.email, that.email)
                && Objects.equals(this.function, that.function)
                && Objects.equals(this.name, that.name)
                && Objects.equals(this.prettyName, that.prettyName)
                && Objects.equals(this.groupMembers, that.groupMembers)
                && Objects.equals(this.superGroup, that.superGroup)
                && Objects.equals(this.websites, that.websites);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id,
                this.becomesActive,
                this.becomesInactive,
                this.description,
                this.email,
                this.function,
                this.active,
                this.name,
                this.prettyName,
                this.groupMembers,
                this.superGroup,
                this.websites);
    }

    @Override
    public String toString() {
        return "FKITGroupDTO{"
                + "id='" + id + '\''
                + ", becomesActive=" + becomesActive
                + ", becomesInactive=" + becomesInactive
                + ", text=" + description
                + ", email='" + email + '\''
                + ", function=" + function
                + ", isActive=" + active
                + ", name='" + name + '\''
                + ", prettyName='" + prettyName + '\''
                + ", groupMembers=" + groupMembers
                + ", superGroup=" + superGroup
                + ", websites=" + websites
                + '}';
    }
}
