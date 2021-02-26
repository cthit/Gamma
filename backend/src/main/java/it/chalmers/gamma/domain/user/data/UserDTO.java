package it.chalmers.gamma.domain.user.data;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.DTO;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.Language;
import it.chalmers.gamma.domain.user.UserId;

import java.time.Year;
import java.util.UUID;

public class UserDTO implements DTO {

    private final UserId id;
    private final Cid cid;
    private final Email email;
    private final Language language;
    private final String nick;
    private final String firstName;
    private final String lastName;
    private final String phone;
    private final String avatarUrl;
    private final boolean userAgreement;
    private final Year acceptanceYear;
    private final boolean activated;

    public UserDTO(UserId id,
                   Cid cid,
                   Email email,
                   Language language,
                   String nick,
                   String firstName,
                   String lastName,
                   String phone,
                   String avatarUrl,
                   boolean userAgreement,
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
        this.userAgreement = userAgreement;
        this.acceptanceYear = acceptanceYear;
        this.activated = activated;
    }

    public UserId getId() {
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

        private UserId id;
        private Cid cid;
        private Email email;
        private Language language;
        private String nick;
        private String firstName;
        private String lastName;
        private String phone;
        private String avatarUrl;
        private boolean userAgreement;
        private Year acceptanceYear;
        private boolean activated;

        public UserDTOBuilder id(UserId id) {
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

        public UserDTOBuilder userAgreement(boolean userAgreement) {
            this.userAgreement = userAgreement;
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
                    userAgreement,
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
            this.userAgreement = user.userAgreement;
            this.acceptanceYear = user.acceptanceYear;
            this.activated = user.activated;
            return this;
        }
    }

}