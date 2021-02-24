package it.chalmers.gamma.domain.group.data;

import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.text.data.db.Text;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Note that this class doesn't implement supergroup. That's up to GroupDTO and GroupShallowDTO
 */
public abstract class GroupBaseDTO {

    private final GroupId id;
    private final Calendar becomesActive;
    private final Calendar becomesInactive;
    private final Email email;
    private final String name;
    private final String prettyName;
    private final String avatarURL;

    protected GroupBaseDTO(GroupId id,
                           Calendar becomesActive,
                           Calendar becomesInactive,
                           Email email,
                           String name,
                           String prettyName,
                           String avatarURL) {
        this.id = id;
        this.becomesActive = becomesActive;
        this.becomesInactive = becomesInactive;
        this.email = email;
        this.name = name;
        this.prettyName = prettyName;
        this.avatarURL = avatarURL;
    }

    public GroupId getId() {
        return id;
    }

    public Calendar getBecomesActive() {
        return becomesActive;
    }

    public Calendar getBecomesInactive() {
        return becomesInactive;
    }

    public Email getEmail() {
        return email;
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

        protected GroupId id;
        protected Calendar becomesActive;
        protected Calendar becomesInactive;
        protected Email email;
        protected String name;
        protected String prettyName;
        protected String avatarURL;

        public abstract T build();
        protected abstract U getThis();

        public U from(T g) {
            this.id = g.getId();
            this.becomesActive = g.getBecomesActive();
            this.becomesInactive = g.getBecomesInactive();
            this.email = g.getEmail();
            this.name = g.getName();
            this.prettyName = g.getPrettyName();
            this.avatarURL = g.getAvatarURL();
            return getThis();
        }

        public U id(GroupId id) {
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

        public U email(Email email) {
            this.email = email;
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
