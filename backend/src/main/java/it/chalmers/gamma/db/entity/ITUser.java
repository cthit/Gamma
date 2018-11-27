package it.chalmers.gamma.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.chalmers.gamma.domain.Language;
import java.time.Instant;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "ituser")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ITUser implements UserDetails {

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

    @Column(name = "language", length = 15)
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(name = "avatarUrl", length = 255)
    private String avatarUrl;

    @Column(name = "gdpr", nullable = false)
    private boolean gdpr;

    @Column(name = "user_agreement", nullable = false)
    private boolean userAgreement;

    @Column(name = "account_locked", nullable = false)
    private boolean accountLocked;

    @Column(name = "acceptance_year", nullable = false)
    private int acceptanceYear;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "last_modified_at", nullable = false)
    private Instant lastModifiedAt;

    @Transient
    private List<GrantedAuthority> authorities;


    public ITUser() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.lastModifiedAt = Instant.now();
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCid() {
        return this.cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return new ArrayList<>(this.authorities);
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.cid;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNick() {
        return this.nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Language getLanguage() {
        return this.language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isGdpr() {
        return this.gdpr;
    }

    public void setGdpr(boolean gdpr) {
        this.gdpr = gdpr;
    }

    public boolean isUserAgreement() {
        return this.userAgreement;
    }

    public void setUserAgreement(boolean userAgreement) {
        this.userAgreement = userAgreement;
    }

    public Year getAcceptanceYear() {
        return Year.of(this.acceptanceYear);
    }

    public void setAcceptanceYear(Year acceptanceYear) {
        this.acceptanceYear = acceptanceYear.getValue();
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastModifiedAt() {
        return this.lastModifiedAt;
    }

    public void setLastModifiedAt(Instant lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public boolean isAccountLocked() {
        return this.accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public void setAuthority(List<GrantedAuthority> authority) {
        this.authorities = new ArrayList<>(authority);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ITUser itUser = (ITUser) o;
        return this.gdpr == itUser.gdpr
                && this.userAgreement == itUser.userAgreement
                && Objects.equals(this.id, itUser.id)
                && Objects.equals(this.cid, itUser.cid)
                && Objects.equals(this.nick, itUser.nick)
                && Objects.equals(this.password, itUser.password)
                && Objects.equals(this.firstName, itUser.firstName)
                && Objects.equals(this.lastName, itUser.lastName)
                && Objects.equals(this.email, itUser.email)
                && Objects.equals(this.phone, itUser.phone)
                && Objects.equals(this.language, itUser.language)
                && Objects.equals(this.avatarUrl, itUser.avatarUrl)
                && Objects.equals(this.acceptanceYear, itUser.acceptanceYear)
                && Objects.equals(this.createdAt, itUser.createdAt)
                && Objects.equals(this.lastModifiedAt, itUser.lastModifiedAt);
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
                + ", authorities=" + authorities
                + '}';
    }
}
