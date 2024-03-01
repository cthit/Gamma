package it.chalmers.gamma.domain.dto.group;

import it.chalmers.gamma.db.entity.Text;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("PMD.ExcessiveParameterList")
public class FKITGroupDTO {

    private final UUID id;
    private final Calendar becomesActive;
    private final Calendar becomesInactive;
    private final Text description;
    private final String email;
    private final Text function;
    private final String name;
    private final String prettyName;
    private final String avatarURL;
    private final FKITSuperGroupDTO superGroup;

    public FKITGroupDTO(UUID id,
                        Calendar becomesActive,
                        Calendar becomesInactive,
                        Text description,
                        String email,
                        Text function,
                        String name,
                        String prettyName,
                        String avatarURL,
                        FKITSuperGroupDTO superGroup) {
        this.id = id;
        this.becomesActive = becomesActive;
        this.becomesInactive = becomesInactive;
        this.description = description;
        this.email = email;
        this.function = function;
        this.name = name;
        this.prettyName = prettyName;
        this.avatarURL = avatarURL;
        this.superGroup = superGroup;
    }

    public FKITGroupDTO(Calendar becomesActive,
                        Calendar becomesInactive,
                        Text description,
                        String email,
                        Text function,
                        String name,
                        String prettyName,
                        String avatarURL,
                        FKITSuperGroupDTO superGroup) {
        this(null,
                becomesActive,
                becomesInactive,
                description,
                email,
                function,
                name,
                prettyName,
                avatarURL,
                superGroup);

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
        Calendar now = new GregorianCalendar();
        return now.after(this.becomesActive) && now.before(this.becomesInactive);
    }

    public String getName() {
        return this.name;
    }

    public String getPrettyName() {
        return this.prettyName;
    }

    public String getAvatarURL() {
        return this.avatarURL;
    }

    public FKITSuperGroupDTO getSuperGroup() {
        return this.superGroup;
    }

    public FKITMinifiedGroupDTO toMinifiedDTO() {
        return new FKITMinifiedGroupDTO(
            this.name, this.function, this.email, this.description, this.id, this.prettyName
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FKITGroupDTO groupDTO = (FKITGroupDTO) o;
        return Objects.equals(this.id, groupDTO.id)
                && Objects.equals(this.becomesActive, groupDTO.becomesActive)
                && Objects.equals(this.becomesInactive, groupDTO.becomesInactive)
                && Objects.equals(this.description, groupDTO.description)
                && Objects.equals(this.email, groupDTO.email)
                && Objects.equals(this.function, groupDTO.function)
                && Objects.equals(this.name, groupDTO.name)
                && Objects.equals(this.prettyName, groupDTO.prettyName)
                && Objects.equals(this.avatarURL, groupDTO.avatarURL)
                && Objects.equals(this.superGroup, groupDTO.superGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.id,
                this.becomesActive,
                this.becomesInactive,
                this.description,
                this.email,
                this.function,
                this.name,
                this.prettyName,
                this.avatarURL,
                this.superGroup);

    }

    @Override
    public String toString() {
        return "FKITGroupDTO{"
                + "id=" + this.id
                + ", becomesActive=" + this.becomesActive
                + ", becomesInactive=" + this.becomesInactive
                + ", description=" + this.description
                + ", email='" + this.email + '\''
                + ", function=" + this.function
                + ", name='" + this.name + '\''
                + ", prettyName='" + this.prettyName + '\''
                + ", avatarURL='" + this.avatarURL + '\''
                + ", superGroup=" + this.superGroup
                + '}';
    }
}
