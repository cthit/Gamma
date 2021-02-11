package it.chalmers.gamma.domain.user.data;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.Language;

import java.time.Year;
import java.util.UUID;

@SuppressWarnings({"PMD.ExcessiveParameterList", "PMD.TooManyFields"})
public class UserDTO {
    private final UUID id;
    private final Cid cid;
    private final Email email;
    private final Language language;
    private final String nick;
    private final String firstName;
    private final String lastName;
    private final String phone;
    private final String avatarUrl;
    private final boolean gdpr;
    private final boolean userAgreement;
    private final boolean accountLocked;
    private final Year acceptanceYear;
    private final boolean activated;

    public UserDTO(UUID id,
                   Cid cid,
                   Email email,
                   Language language,
                   String nick,
                   String firstName,
                   String lastName,
                   String phone,
                   String avatarUrl,
                   boolean gdpr,
                   boolean userAgreement,
                   boolean accountLocked,
                   Year acceptanceYear,
                   boolean activated) {
        this.id = id;
        this.cid = cid;
        this.email = email;
        this.language = language;
        this.nick = nick;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.gdpr = gdpr;
        this.userAgreement = userAgreement;
        this.accountLocked = accountLocked;
        this.acceptanceYear = acceptanceYear;
        this.activated = activated;
    }

    public UUID getId() {
        return id;
    }

    public Cid getCid() {
        return cid;
    }

    public Email getEmail() {
        return email;
    }

    public Language getLanguage() {
        return language;
    }

    public String getNick() {
        return nick;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public boolean isGdpr() {
        return gdpr;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public boolean isUserAgreement() {
        return userAgreement;
    }

    public Year getAcceptanceYear() {
        return acceptanceYear;
    }

    public boolean isActivated() {
        return activated;
    }

    public static class UserDTOBuilder {

        private UUID id;
        private Cid cid;
        private Email email;
        private Language language;
        private String nick;
        private String firstName;
        private String lastName;
        private String phone;
        private String avatarUrl;
        private boolean gdpr;
        private boolean userAgreement;
        private boolean accountLocked;
        private Year acceptanceYear;
        private boolean activated;

        public UserDTOBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public UserDTOBuilder cid(Cid cid) {
            this.cid = cid;
            return this;
        }

        public UserDTOBuilder email(Email email) {
            this.email = email;
            return this;
        }

        public UserDTOBuilder language(Language language) {
            this.language = language;
            return this;
        }

        public UserDTOBuilder nick(String nick) {
            this.nick = nick;
            return this;
        }

        public UserDTOBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserDTOBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserDTOBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserDTOBuilder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public UserDTOBuilder gdpr(boolean gdpr) {
            this.gdpr = gdpr;
            return this;
        }

        public UserDTOBuilder userAgreement(boolean userAgreement) {
            this.userAgreement = userAgreement;
            return this;
        }

        public UserDTOBuilder accountLocked(boolean accountLocked) {
            this.accountLocked = accountLocked;
            return this;
        }

        public UserDTOBuilder acceptanceYear(Year acceptanceYear) {
            this.acceptanceYear = acceptanceYear;
            return this;
        }

        public UserDTOBuilder activated(boolean activated) {
            this.activated = activated;
            return this;
        }

        public UserDTO build() {
            return new UserDTO(
                    id,
                    cid,
                    email,
                    language,
                    nick,
                    firstName,
                    lastName,
                    phone,
                    avatarUrl,
                    gdpr,
                    userAgreement,
                    accountLocked,
                    acceptanceYear,
                    activated
            );
        }

        public UserDTOBuilder from(UserDTO user) {
            this.id = user.id;
            this.cid = user.cid;
            this.email = user.email;
            this.language = user.language;
            this.nick = user.nick;
            this.firstName = user.firstName;
            this.lastName = user.lastName;
            this.phone = user.phone;
            this.avatarUrl = user.avatarUrl;
            this.gdpr = user.gdpr;
            this.userAgreement = user.userAgreement;
            this.accountLocked = user.accountLocked;
            this.acceptanceYear = user.acceptanceYear;
            this.activated = user.activated;
            return this;
        }
    }
    
}
