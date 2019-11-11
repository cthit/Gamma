package it.chalmers.gamma.response.view;

import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.views.WebsiteView;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FKITGroupView {

    private final UUID id;
    private final Calendar becomesActive;
    private final Calendar becomesInactive;
    private final Text description;
    private final String email;
    private final Text function;
    private final boolean isActive;
    private final String name;
    private final String prettyName;
    private List<MembershipView> groupMembers;
    private final FKITSuperGroup superGroup;
    private final List<WebsiteView> websites;


    public FKITGroupView(UUID id,
                         Calendar becomesActive,
                         Calendar becomesInactive,
                         Text description,
                         String email,
                         Text function,
                         boolean isActive,
                         String name,
                         String prettyName,
                         List<MembershipView> groupMembers,
                         FKITSuperGroup superGroup,
                         List<WebsiteView> websites) {
        this.id = id;
        this.becomesActive = becomesActive;
        this.becomesInactive = becomesInactive;
        this.description = description;
        this.email = email;
        this.function = function;
        this.isActive = isActive;
        this.name = name;
        this.prettyName = prettyName;
        this.groupMembers = groupMembers;
        this.superGroup = superGroup;
        this.websites = websites;
    }

    public UUID getId() {
        return id;
    }

    public Calendar getBecomesActive() {
        return becomesActive;
    }

    public Calendar getBecomesInactive() {
        return becomesInactive;
    }

    public Text getDescription() {
        return description;
    }

    public String getEmail() {
        return email;
    }

    public Text getFunction() {
        return function;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getName() {
        return name;
    }

    public String getPrettyName() {
        return prettyName;
    }

    public List<MembershipView> getGroupMembers() {
        return groupMembers;
    }

    public FKITSuperGroup getSuperGroup() {
        return superGroup;
    }

    public List<WebsiteView> getWebsites() {
        return websites;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FKITGroupView that = (FKITGroupView) o;
        return isActive == that.isActive &&
                Objects.equals(id, that.id) &&
                Objects.equals(becomesActive, that.becomesActive) &&
                Objects.equals(becomesInactive, that.becomesInactive) &&
                Objects.equals(description, that.description) &&
                Objects.equals(email, that.email) &&
                Objects.equals(function, that.function) &&
                Objects.equals(name, that.name) &&
                Objects.equals(prettyName, that.prettyName) &&
                Objects.equals(groupMembers, that.groupMembers) &&
                Objects.equals(superGroup, that.superGroup) &&
                Objects.equals(websites, that.websites);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(id, becomesActive, becomesInactive, description, email, function, isActive, name, prettyName, groupMembers, superGroup, websites);
    }

    @Override
    public String toString() {
        return "FKITGroupView{" +
                "id='" + id + '\'' +
                ", becomesActive=" + becomesActive +
                ", becomesInactive=" + becomesInactive +
                ", text=" + description +
                ", email='" + email + '\'' +
                ", function=" + function +
                ", isActive=" + isActive +
                ", name='" + name + '\'' +
                ", prettyName='" + prettyName + '\'' +
                ", groupMembers=" + groupMembers +
                ", superGroup=" + superGroup +
                ", websites=" + websites +
                '}';
    }
}
