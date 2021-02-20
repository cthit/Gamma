package it.chalmers.gamma.domain.user.data;

import it.chalmers.gamma.domain.*;
import it.chalmers.gamma.domain.user.UserId;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.*;

@Entity
@Table(name = "ituser")
public class User implements GEntity<UserDTO> {

    @EmbeddedId
    private UserId id;

    @Embedded
    private Cid cid;

    @Column(name = "password")
    private String password;

    @Column(name = "nick")
    private String nick;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Embedded
    private Email email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "gdpr")
    private boolean gdpr;

    @Column(name = "user_agreement")
    private boolean userAgreement;

    @Column(name = "account_locked")
    private boolean accountLocked;

    @Column(name = "activated")
    private boolean activated;

    @Column(name = "acceptance_year")
    private int acceptanceYear;

    protected User() { }

    public User(UserDTO user) {
        try {
            this.apply(user);
        } catch (IDsNotMatchingException e) {
            e.printStackTrace();
        }

        if(this.id == null) {
            this.id = new UserId();
        }
    }

    public UserId getId() {
        return id;
    }

    public void setId(UserId id) {
        this.id = id;
    }

    public Cid getCid() {
        return cid;
    }

    public void setCid(Cid cid) {
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

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
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
        return "User{" +
                "id=" + id +
                ", cid='" + cid + '\'' +
                ", password='" + password + '\'' +
                ", nick='" + nick + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", language=" + language +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", gdpr=" + gdpr +
                ", userAgreement=" + userAgreement +
                ", accountLocked=" + accountLocked +
                ", activated=" + activated +
                ", acceptanceYear=" + acceptanceYear +
                '}';
    }

    @Override
    public void apply(UserDTO u) throws IDsNotMatchingException {
        if(this.id != null && this.id != u.getId()) {
            throw new IDsNotMatchingException();
        }

        this.id = u.getId();
        this.acceptanceYear = u.getAcceptanceYear().getValue();
        this.activated = u.isActivated();
        this.avatarUrl = u.getAvatarUrl();
        this.accountLocked = u.isAccountLocked();
        this.cid = u.getCid();
        this.email = u.getEmail();
        this.firstName = u.getFirstName();
        this.lastName = u.getLastName();
        this.gdpr = u.isGdpr();
        this.language = u.getLanguage();
        this.nick = u.getNick();
        this.userAgreement = u.isUserAgreement();
        this.phone = u.getPhone();
    }
}
