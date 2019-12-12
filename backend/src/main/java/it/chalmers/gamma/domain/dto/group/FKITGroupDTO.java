package it.chalmers.gamma.domain.dto.group;

import it.chalmers.gamma.db.entity.Text;

import java.util.Calendar;
import java.util.GregorianCalendar;
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

    public FKITGroupDTO(UUID id,
                        Calendar becomesActive,
                        Calendar becomesInactive,
                        Text description,
                        String email,
                        Text function,
                        String name,
                        String prettyName, String avatarURL) {
        this.id = id;
        this.becomesActive = becomesActive;
        this.becomesInactive = becomesInactive;
        this.description = description;
        this.email = email;
        this.function = function;
        this.name = name;
        this.prettyName = prettyName;
        this.avatarURL = avatarURL;
    }

    public FKITGroupDTO(Calendar becomesActive,
                        Calendar becomesInactive,
                        Text description,
                        String email,
                        Text function,
                        String name,
                        String prettyName, String avatarURL) {
        this(null, becomesActive, becomesInactive, description, email, function, name, prettyName, avatarURL);

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

    public FKITMinifiedGroupDTO toMinifiedDTO() {
        return new FKITMinifiedGroupDTO(
            this.name, this.function, this.email, this.description, this.id
        );
    }

}
