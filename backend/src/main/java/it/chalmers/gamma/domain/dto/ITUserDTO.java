package it.chalmers.gamma.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.chalmers.gamma.domain.Language;
import java.time.Year;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ITUserDTO {
    private final UUID id;
    private final String cid;
    private final String nick;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final Language language;
    private final String avatarUrl;
    private final boolean gdpr;
    private final boolean userAgreement;
    private final boolean accountLocked;
    private final Year acceptanceYear;

    public ITUserDTO(UUID id,
                     String cid,
                     String nick,
                     String firstName,
                     String lastName,
                     String email,
                     String phone,
                     Language language,
                     String avatarUrl,
                     boolean gdpr,
                     boolean userAgreement,
                     boolean accountLocked,
                     Year acceptanceYear) {
        this.id = id;
        this.cid = cid;
        this.nick = nick;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.language = language;
        this.avatarUrl = avatarUrl;
        this.gdpr = gdpr;
        this.userAgreement = userAgreement;
        this.accountLocked = accountLocked;
        this.acceptanceYear = acceptanceYear;
    }

    public UUID getId() {
        return this.id;
    }

    public String getCid() {
        return this.cid;
    }

    public String getNick() {
        return this.nick;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhone() {
        return this.phone;
    }

    public Language getLanguage() {
        return this.language;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public boolean isGdpr() {
        return this.gdpr;
    }

    public boolean isUserAgreement() {
        return this.userAgreement;
    }

    public boolean isAccountLocked() {
        return this.accountLocked;
    }

    public Year getAcceptanceYear() {
        return this.acceptanceYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ITUserDTO that = (ITUserDTO) o;
        return this.gdpr == that.gdpr
                && this.userAgreement == that.userAgreement
                && this.accountLocked == that.accountLocked
                && Objects.equals(this.id, that.id)
                && Objects.equals(this.cid, that.cid)
                && Objects.equals(this.nick, that.nick)
                && Objects.equals(this.firstName, that.firstName)
                && Objects.equals(this.lastName, that.lastName)
                && Objects.equals(this.email, that.email)
                && Objects.equals(this.phone, that.phone)
                && this.language == that.language
                && Objects.equals(this.avatarUrl, that.avatarUrl)
                && Objects.equals(this.acceptanceYear, that.acceptanceYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id,
                this.cid,
                this.nick,
                this.firstName,
                this.lastName,
                this.email,
                this.phone,
                this.language,
                this.avatarUrl,
                this.gdpr,
                this.userAgreement,
                this.accountLocked,
                this.acceptanceYear);
    }

    @Override
    public String toString() {
        return "ITUserDTO{"
                + "id=" + this.id
                + ", cid='" + this.cid + '\''
                + ", nick='" + this.nick + '\''
                + ", firstName='" + this.firstName + '\''
                + ", lastName='" + this.lastName + '\''
                + ", email='" + this.email + '\''
                + ", phone='" + this.phone + '\''
                + ", language=" + this.language
                + ", avatarUrl='" + this.avatarUrl + '\''
                + ", gdpr=" + this.gdpr
                + ", userAgreement=" + this.userAgreement
                + ", accountLocked=" + this.accountLocked
                + ", acceptanceYear=" + this.acceptanceYear
                + '}';
    }
}
