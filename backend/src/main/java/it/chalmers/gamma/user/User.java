package it.chalmers.gamma.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import it.chalmers.gamma.domain.Language;

import java.time.Instant;
import java.time.Year;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "ituser")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@SuppressWarnings({"PMD.TooManyFields"})
public class User {

    @Id
    @Column(updatable = false)
    @JsonIgnore
    private UUID id;

    @Column(name = "cid", length = 10, nullable = false, unique = true)
    private String cid;

    @JsonIgnore
    @Column(name = "password", length = 255, nullable = false)
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

    @Column(name = "created_at", nullable = false)
    @ColumnDefault("current_timestamp")
    private Instant createdAt;

    @Column(name = "last_modified_at", nullable = false)
    @ColumnDefault("current_timestamp")
    private Instant lastModifiedAt;

    protected User() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.lastModifiedAt = Instant.now();
        this.activated = true;
        this.avatarUrl = "default.jpg";
    }

    protected UserDTO toDTO() {
        return new UserDTO(
                this.id,
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
                Year.of(this.acceptanceYear),
                this.activated
        );
    }

    protected UserDTO toUserDetailsDTO(List<GrantedAuthority> authorities) {
        return new UserDTO(
                this.id,
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
                Year.of(this.acceptanceYear),
                authorities,
                this.password,
                this.activated
        );
    }

    protected UUID getId() {
        return this.id;
    }

    protected void setId(UUID id) {
        this.id = id;
    }

    protected String getCid() {
        return this.cid;
    }

    protected void setCid(String cid) {
        this.cid = cid.toLowerCase();
    }

    protected void setPassword(String password) {
        this.password = password;
    }

    protected String getNick() {
        return this.nick;
    }

    protected void setNick(String nick) {
        this.nick = nick;
    }

    protected String getFirstName() {
        return this.firstName;
    }

    protected void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    protected void setLastName(String lastName) {
        this.lastName = lastName;
    }

    protected String getEmail() {
        return this.email;
    }

    protected void setEmail(String email) {
        this.email = email;
    }

    protected String getPhone() {
        return this.phone;
    }

    protected void setPhone(String phone) {
        this.phone = phone;
    }

    protected Language getLanguage() {
        return this.language;
    }

    protected void setLanguage(Language language) {
        this.language = language;
    }

    protected String getAvatarUrl() {
        return this.avatarUrl;
    }

    protected void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    protected boolean isGdpr() {
        return this.gdpr;
    }

    protected void setGdpr(boolean gdpr) {
        this.gdpr = gdpr;
    }

    protected boolean isUserAgreement() {
        return this.userAgreement;
    }

    protected void setUserAgreement(boolean userAgreement) {
        this.userAgreement = userAgreement;
    }

    protected Year getAcceptanceYear() {
        return Year.of(this.acceptanceYear);
    }

    protected void setAcceptanceYear(Year acceptanceYear) {
        this.acceptanceYear = acceptanceYear.getValue();
    }

    protected Instant getCreatedAt() {
        return this.createdAt;
    }

    protected void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    protected Instant getLastModifiedAt() {
        return this.lastModifiedAt;
    }

    protected void setLastModifiedAt(Instant lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    protected boolean isAccountLocked() {
        return this.accountLocked;
    }

    protected void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    protected boolean isActivated() {
        return this.activated;
    }

    protected void setActivated(boolean activated) {
        this.activated = activated;
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
                && Objects.equals(this.acceptanceYear, user.acceptanceYear)
                && Objects.equals(this.createdAt, user.createdAt)
                && Objects.equals(this.lastModifiedAt, user.lastModifiedAt);
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
                this.acceptanceYear,
                this.createdAt,
                this.lastModifiedAt);
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
                + ", createdAt=" + createdAt
                + ", lastModifiedAt=" + lastModifiedAt
                + '}';
    }

}
