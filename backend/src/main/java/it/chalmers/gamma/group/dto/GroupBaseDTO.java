package it.chalmers.gamma.group.dto;

import it.chalmers.gamma.domain.text.Text;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * Note that this class doesn't implement supergroup. That's up to GroupDTO and GroupShallowDTO
 */
public abstract class GroupBaseDTO {

    private final UUID id;
    private final Calendar becomesActive;
    private final Calendar becomesInactive;
    private final Text description;
    private final String email;
    private final Text function;
    private final String name;
    private final String prettyName;
    private final String avatarURL;

    protected GroupBaseDTO(UUID id,
                     Calendar becomesActive,
                     Calendar becomesInactive,
                     Text description,
                     String email,
                     Text function,
                     String name,
                     String prettyName,
                     String avatarURL) {
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

    public String getName() {
        return name;
    }

    public String getPrettyName() {
        return prettyName;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public boolean isActive() {
        Calendar now = new GregorianCalendar();
        return now.after(this.becomesActive) && now.before(this.becomesInactive);
    }

    protected abstract static class GroupBaseDTOBuilder<T extends GroupBaseDTO, U extends GroupBaseDTOBuilder<T, U>> {

        protected UUID id;
        protected Calendar becomesActive;
        protected Calendar becomesInactive;
        protected Text description;
        protected String email;
        protected Text function;
        protected String name;
        protected String prettyName;
        protected String avatarURL;

        public abstract T build();
        protected abstract U getThis();

        public U from(T g) {
            this.id = g.getId();
            this.becomesActive = g.getBecomesActive();
            this.becomesInactive = g.getBecomesInactive();
            this.description = g.getDescription();
            this.email = g.getEmail();
            this.function = g.getFunction();
            this.name = g.getName();
            this.prettyName = g.getPrettyName();
            this.avatarURL = g.getAvatarURL();
            return getThis();
        }

        public U id(UUID id) {
            this.id = id;
            return getThis();
        }

        public U becomesActive(Calendar becomesActive) {
            this.becomesActive = becomesActive;
            return getThis();
        }

        public U becomesInactive(Calendar becomesInactive) {
            this.becomesInactive = becomesInactive;
            return getThis();
        }

        public U description(Text description) {
            this.description = description;
            return getThis();
        }

        public U email(String email) {
            this.email = email;
            return getThis();
        }

        public U function(Text function) {
            this.function = function;
            return getThis();
        }

        public U name(String name) {
            this.name = name;
            return getThis();
        }

        public U prettyName(String prettyName) {
            this.prettyName = prettyName;
            return getThis();
        }

        public U avatarUrl(String avatarURL) {
            this.avatarURL = avatarURL;
            return getThis();
        }

    }

}