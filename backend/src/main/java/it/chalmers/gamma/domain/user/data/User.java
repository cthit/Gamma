package it.chalmers.gamma.domain.user.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import it.chalmers.gamma.domain.GEntity;
import it.chalmers.gamma.domain.IDsNotMatchingException;
import it.chalmers.gamma.domain.Language;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "ituser")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class User implements GEntity<UserDTO> {

    @Id
    @Column(updatable = false)
    @JsonIgnore
    private UUID id;

    @Column(name = "cid", length = 10, nullable = false, unique = true)
    private String cid;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nick", length = 50)
    private String nick;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "language", length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(name = "avatar_url", length = 255, nullable = false)
    @ColumnDefault("default.jpg")
    private String avatarUrl;

    @Column(name = "gdpr", nullable = false)
    @ColumnDefault("false")
    private boolean gdpr;

    @Column(name = "user_agreement", nullable = false)
    @ColumnDefault("false")
    private boolean userAgreement;

    @Column(name = "account_locked", nullable = false)
    @ColumnDefault("false")
    private boolean accountLocked;

    @Column(name = "activated", nullable = false)
    private boolean activated;

    @Column(name = "acceptance_year", nullable = false)
    private int acceptanceYear;

    public User() { }

    public User(UserDTO user) {
        try {
            this.apply(user);
        } catch (IDsNotMatchingException e) {
            e.printStackTrace();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isGdpr() {
        return gdpr;
    }

    public void setGdpr(boolean gdpr) {
        this.gdpr = gdpr;
    }

    public boolean isUserAgreement() {
        return userAgreement;
    }

    public void setUserAgreement(boolean userAgreement) {
        this.userAgreement = userAgreement;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public int getAcceptanceYear() {
        return acceptanceYear;
    }

    public void setAcceptanceYear(int acceptanceYear) {
        this.acceptanceYear = acceptanceYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return this.gdpr == user.gdpr
                && this.userAgreement == user.userAgreement
                && Objects.equals(this.id, user.id)
                && Objects.equals(this.cid, user.cid)
                && Objects.equals(this.nick, user.nick)
                && Objects.equals(this.password, user.password)
                && Objects.equals(this.firstName, user.firstName)
                && Objects.equals(this.lastName, user.lastName)
                && Objects.equals(this.email, user.email)
                && Objects.equals(this.phone, user.phone)
                && Objects.equals(this.language, user.language)
                && Objects.equals(this.avatarUrl, user.avatarUrl)
                && Objects.equals(this.acceptanceYear, user.acceptanceYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.id,
                this.cid,
                this.password,
                this.nick,
                this.firstName,
                this.lastName,
                this.email,
                this.phone,
                this.language,
                this.avatarUrl,
                this.gdpr,
                this.userAgreement,
                this.acceptanceYear);
    }

    @Override
    public String toString() {
        return "ITUser{"
                + "id=" + id
                + ", cid='" + cid + '\''
                + ", password='" + "<redacted>" + '\''
                + ", nick='" + nick + '\''
                + ", firstName='" + firstName + '\''
                + ", lastName='" + lastName + '\''
                + ", email='" + email + '\''
                + ", phone='" + phone + '\''
                + ", language=" + language
                + ", avatarUrl='" + avatarUrl + '\''
                + ", gdpr=" + gdpr
                + ", userAgreement=" + userAgreement
                + ", accountLocked=" + accountLocked
                + ", acceptanceYear=" + acceptanceYear
                + '}';
    }

    @Override
    public void apply(UserDTO u) throws IDsNotMatchingException {
        if(this.id != u.getId()) {
            throw new IDsNotMatchingException();
        }

        this.acceptanceYear = u.getAcceptanceYear().getValue();
        this.activated = u.isActivated();
        this.avatarUrl = u.getAvatarUrl();
        this.accountLocked = u.isAccountLocked();
        this.cid = u.getCid().value;
        this.email = u.getEmail().value;
        this.firstName = u.getFirstName();
        this.lastName = u.getLastName();
        this.gdpr = u.isGdpr();
        this.language = u.getLanguage();
        this.nick = u.getNick();
        this.userAgreement = u.isUserAgreement();
        this.phone = u.getPhone();
    }
}
