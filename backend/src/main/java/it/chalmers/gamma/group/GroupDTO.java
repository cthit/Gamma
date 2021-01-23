package it.chalmers.gamma.group;

import it.chalmers.gamma.domain.text.Text;
import it.chalmers.gamma.supergroup.SuperGroupDTO;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

public class GroupDTO {

    private final UUID id;
    private final Calendar becomesActive;
    private final Calendar becomesInactive;
    private final Text description;
    private final String email;
    private final Text function;
    private final String name;
    private final String prettyName;
    private final String avatarURL;
    private final SuperGroupDTO superGroup;

    private GroupDTO(UUID id,
                    Calendar becomesActive,
                    Calendar becomesInactive,
                    Text description,
                    String email,
                    Text function,
                    String name,
                    String prettyName,
                    String avatarURL,
                    SuperGroupDTO superGroup) {
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

    public SuperGroupDTO getSuperGroup() {
        return superGroup;
    }

    public boolean isActive() {
        Calendar now = new GregorianCalendar();
        return now.after(this.becomesActive) && now.before(this.becomesInactive);
    }

    public static class GroupDTOBuilder {

        private UUID id;
        private Calendar becomesActive;
        private Calendar becomesInactive;
        private Text description;
        private String email;
        private Text function;
        private String name;
        private String prettyName;
        private String avatarURL;
        private SuperGroupDTO superGroup;

        public GroupDTOBuilder from(GroupDTO g) {
            this.id = g.getId();
            this.becomesActive = g.getBecomesActive();
            this.becomesInactive = g.getBecomesInactive();
            this.description = g.getDescription();
            this.email = g.getEmail();
            this.function = g.getFunction();
            this.name = g.getName();
            this.prettyName = g.getPrettyName();
            this.avatarURL = g.getAvatarURL();
            this.superGroup = g.getSuperGroup();
            return this;
        }

        public GroupDTOBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public GroupDTOBuilder becomesActive(Calendar becomesActive) {
            this.becomesActive = becomesActive;
            return this;
        }

        public GroupDTOBuilder becomesInactive(Calendar becomesInactive) {
            this.becomesInactive = becomesInactive;
            return this;
        }

        public GroupDTOBuilder description(Text description) {
            this.description = description;
            return this;
        }

        public GroupDTOBuilder email(String email) {
            this.email = email;
            return this;
        }

        public GroupDTOBuilder function(Text function) {
            this.function = function;
            return this;
        }

        public GroupDTOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public GroupDTOBuilder prettyName(String prettyName) {
            this.prettyName = prettyName;
            return this;
        }

        public GroupDTOBuilder avatarUrl(String avatarURL) {
            this.avatarURL = avatarURL;
            return this;
        }

        public GroupDTOBuilder superGroup(SuperGroupDTO superGroup) {
            this.superGroup = superGroup;
            return this;
        }

        public GroupDTO createGroupDTO() {
            return new GroupDTO(id,
                    becomesActive,
                    becomesInactive,
                    description,
                    email,
                    function,
                    name,
                    prettyName,
                    avatarURL,
                    superGroup
            );
        }
    }

}
